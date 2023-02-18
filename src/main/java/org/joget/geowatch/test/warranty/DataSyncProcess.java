package org.joget.geowatch.test.warranty;

import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.*;
import java.sql.*;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;

public class DataSyncProcess {

    public void execute(){

        Connection con = null;
        Connection sqlconnection = null;
        Statement statement = null;

        String uuid = null;//UuidGenerator.getInstance().getUuid();
        try {
            // retrieve connection from the Custom datasource
            //DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");

            //Build Url for connecting to Oracle DB
            String url = "jdbc:oracle:thin:#envVariable.jdbcURL#";
            String username = "#envVariable.jdbcUserName#";
            String password = "#envVariable.jdbcPassword#";
            //Register Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //Get Connection Using DriverManager
            con = DriverManager.getConnection(url, username, password);
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            // execute SQL query
            if(!con.isClosed()) {
                //Replace Equipment Number everywhere - crossref view
                String replaceEqpmtQuery = "select * from apps.XXOIC_JOGETIN_MO_CROSSREF_V";
                PreparedStatement repEqpstmt = con.prepareStatement(replaceEqpmtQuery);
                ResultSet repEqprs = repEqpstmt.executeQuery();
                FormRowSet rowsEqpHistoryData = new FormRowSet();
                rowsEqpHistoryData.setMultiRow(true);

                while (repEqprs.next()) {
                    String strOrgEqpNo = (repEqprs.getString("ORIGINAL_EQUIPMENT_NUMBER")!= null)?repEqprs.getString("ORIGINAL_EQUIPMENT_NUMBER").toString():"";
                    String strRepEqpNo = (repEqprs.getString("REPLACED_EQUIPMENT_NUMBER")!= null)?repEqprs.getString("REPLACED_EQUIPMENT_NUMBER").toString():"";
                    String strRepOn = (repEqprs.getString("REPLACED_ON")!= null)?repEqprs.getString("REPLACED_ON").toString():"";
                    String strRepBy = (repEqprs.getString("REPLACED_BY")!= null)?repEqprs.getString("REPLACED_BY").toString():"";

                    FormRowSet rowsEqpMasterData = formDataDao.find("equipment_master", "equipment_master", "where c_equipment_Number =?", new Object[] {strOrgEqpNo}, null, null, null, null);
                    if(rowsEqpMasterData != null){
                        LogUtil.info("--- Equipment master  Original Eqp No", " ------- \n"+strOrgEqpNo);
                        for(FormRow	eqrow:rowsEqpMasterData)
                        {
                            eqrow.put("c_equipment_Number",strRepEqpNo);
                        }

                        formDataDao.saveOrUpdate("equipment_master", "equipment_master", rowsEqpMasterData);
                    }

                    FormRowSet rowsEqpWarrantyData = formDataDao.find("warrantyEquipment", "warranty_equipment", "where c_equipment_Number =?", new Object[] {strOrgEqpNo}, null, null, null, null);
                    if(rowsEqpWarrantyData != null){

                        for(FormRow	eqrow:rowsEqpWarrantyData)
                        {
                            eqrow.put("c_equipment_Number",strRepEqpNo);
                        }

                        formDataDao.saveOrUpdate("warrantyEquipment", "warranty_equipment", rowsEqpWarrantyData);
                    }
                    LogUtil.info("--- Begin of Eqp Replacement History", " ------- \n");


                    FormRow row = new FormRow();
                    //Set values
                    uuid = UuidGenerator.getInstance().getUuid();
                    row.setProperty("id", uuid);

                    row.setProperty("original_equipment_number",strOrgEqpNo);
                    row.setProperty("replaced_equipment_Number",strRepEqpNo);
                    row.setProperty("replaced_on",strRepOn);
                    row.setProperty("replaced_by",strRepBy);
                    //Populate the form
                    rowsEqpHistoryData.add(row);
                }
                LogUtil.info("---  End of While Loop", " ------- \n"+rowsEqpHistoryData);
                //String formIdItem = "";
                //String tableNameItem = "";

                formDataDao.saveOrUpdate("equipmentReplacementHistory", "equipment_replaces", rowsEqpHistoryData);
                LogUtil.info("---  End of Eqp Replacement History", " ------- \n");


                //Add tags for DO number and Equipment Number in equipment master table - RTRN_EQUIPMENT view
                String returnedEqpmtQuery = "select * from apps.XXOIC_JOGETIN_RTRN_EQUIPMENT_V";
                PreparedStatement retEqpstmt = con.prepareStatement(returnedEqpmtQuery);
                ResultSet retEqprs = retEqpstmt.executeQuery();
                FormRowSet rowsEqpMasterData = new FormRowSet();
                rowsEqpMasterData.setMultiRow(true);

                while (retEqprs.next()) {
                    String strDoNo = (retEqprs.getString("DO_NUMBER")!= null)?retEqprs.getString("DO_NUMBER").toString():"";
                    String strRetEqpNo = (retEqprs.getString("RETURNED_EQUIPMENT_NUMBER")!= null)?retEqprs.getString("RETURNED_EQUIPMENT_NUMBER").toString():"";
                    String strRetOn = (retEqprs.getString("RETURNED_ON")!= null)?retEqprs.getString("RETURNED_ON").toString():"";
                    String strRetBy = (retEqprs.getString("RETURNED_BY")!= null)?retEqprs.getString("RETURNED_BY").toString():"";

                    FormRowSet rowsEqpMasterData = formDataDao.find("equipment_master", "equipment_master", "where c_equipment_Number =? and c_do_Number=?", new Object[] {strRetEqpNo,strDoNo}, null, null, null, null);
                    if(rowsEqpMasterData != null){
                        LogUtil.info("--- Equipment master - Returned", " ------- \n");
                        for(FormRow	eqrow:rowsEqpMasterData)
                        {
                            eqrow.put("tag","Returned");
                        }

                        formDataDao.saveOrUpdate("equipment_master", "equipment_master", rowsEqpMasterData);
                    }
                    //If the DO number exists in some generated warranty, check if all the equipments under it are returned, if yes, remove the DO number from the warranty
                    //step 1 get the records with returned equipment in warranty - delete them
                    FormRowSet rowsWarrantyEqpData = formDataDao.find("warrantyEquipment", "warranty_equipment", "where c_equipment_Number =?", new Object[] {strRetEqpNo}, null, null, null, null);
                    if(rowsWarrantyEqpData != null){
                        formDatadao.delete("warrantyEquipment", "warranty_equipment",rowsWarrantyEqpData);
                    }
                    //step 2 - check if all the equipments under that DO number are returned i.e. DO number does not exist in warranty_equipment for any other equipment also
                    //If yes, remove the DO number from warranty
                    FormRowSet rowsWarrantyDOData = formDataDao.find("warrantyEquipment", "warranty_equipment", "where c_do_Number =?", new Object[] {strDoNo}, null, null, null, null);
                    if(rowsWarrantyDOData != null && rowsWarrantyDOData.size() == 0){
                        FormRowSet rowsEqpWarrantyData = formDataDao.find("warranty_review_form", "review_form", "where c_rev_status = ? and c_do_no LIKE ?", new Object[] {"Active", "%"+strDoNo+"%"}, null, null, null, null);
                        if(rowsEqpWarrantyData != null){
                            //remove DO number
                            for(FormRow	eqrow:rowsEqpWarrantyData)
                            {
                                String strOrgDONo = eqrow.get("do_no");
                                String strRepDONo = strOrgDONo.replace(strDoNo, "");
                                eqrow.put("do_no",strRepDONo);
                            }

                            formDataDao.saveOrUpdate("review_form", "review_form", rowsEqpWarrantyData);
                        }
                    }

                }

                LogUtil.info("--- Equipment master - End of Returned", " ------- \n");
                //Add tags for DO number and SO Number in DO master table - OLDNEWDO view
                String reinvoiceQuery = "select * from apps.XXOIC_JOGETIN_OLDNEWDO_V";
                PreparedStatement reinvoicestmt = con.prepareStatement(reinvoiceQuery);
                ResultSet reinvoicers = reinvoicestmt.executeQuery();
                FormRowSet rowsDOMasterData = new FormRowSet();
                rowsDOMasterData.setMultiRow(true);

                while (reinvoicers.next()) {
                    String stroldDoNo = (reinvoicers.getString("OLD_DO_NUMBER")!= null)?reinvoicers.getString("OLD_DO_NUMBER").toString():"";
                    String strnewDoNo = (reinvoicers.getString("NEW_DO_NUMBER")!= null)?reinvoicers.getString("NEW_DO_NUMBER").toString():"";
                    String stroldSoNo = (reinvoicers.getString("OLD_SALES_ORDER")!= null)?reinvoicers.getString("OLD_SALES_ORDER").toString():"";
                    String strnewSoNo = (reinvoicers.getString("NEW_SALES_ORDER")!= null)?reinvoicers.getString("NEW_SALES_ORDER").toString():"";

                    FormRowSet rowsDOMasterData = formDataDao.find("do_master_form", "do_master", "where c_do_Number=?", new Object[] {stroldDoNo}, null, null, null, null);
                    if(rowsDOMasterData != null){
                        LogUtil.info("--- DO master - Reinvoice", " ------- \n");
                        for(FormRow	dorow:rowsDOMasterData)
                        {

                            dorow.put("tag","Reinvoiced");
                            dorow.put("original_do_number", stroldDoNo);
                            dorow.put("do_Number", strnewDoNo);
                        }

                        formDataDao.saveOrUpdate("do_master_form", "do_master", rowsDOMasterData);
                    }
                    //If the DO number exists in some generated warranty, should it be replaced?
                    FormRowSet rowsEqpWarrantyData = formDataDao.find("warranty_review_form", "review_form", "where c_rev_status = ? and c_do_no LIKE ? ", new Object[] {"Active", "%"+stroldDoNo+"%"}, null, null, null, null);
                    if(rowsEqpWarrantyData != null){
                        //replace DO number
                        for(FormRow	eqrow:rowsEqpWarrantyData)
                        {
                            eqrow.put("do_no",strnewDoNo);
                        }

                        formDataDao.saveOrUpdate("review_form", "review_form", rowsEqpWarrantyData);
                    }

                    //replacing invoices from replaced DO numbers
                    String repinvoiceQuery = "select * from apps.XXOIC_JOGETIN_REINVOICING_V where DO_NUMBER = ?";
                    PreparedStatement repinvoicestmt = con.prepareStatement(repinvoiceQuery);
                    repinvoicestmt.setString(1, stroldDoNo);
                    ResultSet repinvoices = repinvoicestmt.executeQuery();

                    while (repinvoices.next()) {
                        String strOldInvoice = (repinvoices.getString("INVOICE_NUMBER")!= null)?repinvoices.getString("INVOICE_NUMBER").toString():"";
                        //invoices

                        FormRowSet rowsInvWarrantyData = formDataDao.find("warranty_review_form", "review_form", "where c_rev_status = ? and c_invoices LIKE ? ", new Object[] {"Active", "%"+strOldInvoice+"%"}, null, null, null, null);
                        if(rowsInvWarrantyData != null){
                            //replace DO number
                            for(FormRow	eqrow:rowsInvWarrantyData)
                            {
                                eqrow.put("invoices","");
                            }

                            formDataDao.saveOrUpdate("review_form", "review_form", rowsInvWarrantyData);
                        }
                    }



                }
                LogUtil.info("--- Equipment master - End of Returned", " ------- \n");

            }

        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
        } finally {
            try {
                if(con != null) {
                    con.close();
                }
            } catch(SQLException e) {
            }
            try {
                if(sqlconnection != null) {
                    sqlconnection.close();
                }
            } catch(SQLException e) {
            }
        }
    }


}
