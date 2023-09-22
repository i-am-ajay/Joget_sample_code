package org.joget.geowatch.test.warranty;

import org.joget.apps.app.model.AppDefinition;
import org.joget.workflow.model.WorkflowAssignment;

import javax.servlet.http.HttpServletRequest;
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

public class EquipmentReturnCode {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        String warrantyId = "#process.recordId#";
        String processType = "#variable.processType#";
        if(processType.equals("Reload")){
            warrantyId = "#variable.rId#";

        }


        Connection con = null;
        Connection sqlconnection = null;
        Statement statement = null;
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        String doNumber = null;
        String equipmentNumber = null;

        StringBuilder doString = new StringBuilder();
        StringBuilder equipmentNum = new StringBuilder();
        List doList = new ArrayList();
        List equipmentNumberList = new ArrayList();
        // get do details

        FormRow warranty = formDataDao.load("warranty_review_form", "review_form", warrantyId);
        if(warranty != null ) {
            doNumber = (warranty.get("do_no") != null) ? warranty.get("do_no").toString() : "";
            for (String doNum : doNumber.split(",")) {
                doList.add(doNum);
                //doString.append("'").append(doNum.trim()).append("',");
                FormRowSet rowsEqpMasterData = formDataDao.find("equipment_master", "equipment_master", "where c_do_Number =? ", new Object[] {doNum}, null, null, null, null);
                for(FormRow equip : rowsEqpMasterData){
                    String eNum = equip.getProperty("equipment_Number");

                    if(eNum != null && !eNum.isEmpty()) {
                        equipmentNumberList.add(eNum);
                        //equipmentNum.append("'").append(eNum.trim()).append("',");
                    }
                    //equipmentNum.deleteCharAt(equipmentNum.length()-1);
                }
            }

        }

        if(doList.size() == 0){
            return null;
        }

        for(Object dn : doList){
            doString.append("'").append(dn.toString().trim()).append("',");
        }
        doNumber = doString.deleteCharAt(doString.length()-1).toString();

        for(Object en : equipmentNumberList){
            equipmentNum.append("'").append(en.toString().trim()).append("',");
        }
        if(equipmentNumber != null && equipmentNumber.length() > 1) {
            equipmentNumber = equipmentNum.deleteCharAt(equipmentNum.length() - 1).toString();
        }

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

            // execute SQL query
            if(!con.isClosed()) {
                //Replace Equipment Number everywhere - crossref view
                /*String replaceEqpmtQuery = "select * from apps.XXOIC_JOGETIN_MO_CROSSREF_V where ORIGINAL_EQUIPMENT_NUMBER in ("+equipmentNumber+")";
                //equipmentNumber = "'135544445','4321115433'";
                PreparedStatement repEqpstmt = con.prepareStatement(replaceEqpmtQuery);
                //Array equipmentArray = con.createArrayOf("varchar",equipmentNumberList.toArray());
                //repEqpstmt.setArray(1,equipmentArray);
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

                //String formIdItem = "";
                //String tableNameItem = "";

                formDataDao.saveOrUpdate("equipmentReplacementHistory", "equipment_replaces", rowsEqpHistoryData);*/

                //Array doArray = con.createArrayOf("varchar",doList.toArray());

                //Add tags for DO number and Equipment Number in equipment master table - RTRN_EQUIPMENT view
                String returnedEqpmtQuery = "select * from apps.XXOIC_JOGETIN_RTRN_EQUIPMENT_V where DO_NUMBER in ("+doNumber+")";
                PreparedStatement retEqpstmt = con.prepareStatement(returnedEqpmtQuery);
                //retEqpstmt.setArray(1,doArray);
                //retEqpstmt.setString(1,doNumber);

                ResultSet retEqprs = retEqpstmt.executeQuery();
                FormRowSet rowsEqpMasterData = new FormRowSet();
                rowsEqpMasterData.setMultiRow(true);

                while (retEqprs.next()) {
                    String strDoNo = (retEqprs.getString("DO_NUMBER")!= null)?retEqprs.getString("DO_NUMBER").toString():"";
                    String strRetEqpNo = (retEqprs.getString("RETURNED_EQUIPMENT_NUMBER")!= null)?retEqprs.getString("RETURNED_EQUIPMENT_NUMBER").toString():"";
                    String strRetOn = (retEqprs.getString("RETURNED_ON")!= null)?retEqprs.getString("RETURNED_ON").toString():"";
                    String strRetBy = (retEqprs.getString("RETURNED_BY")!= null)?retEqprs.getString("RETURNED_BY").toString():"";

                    rowsEqpMasterData = formDataDao.find("equipment_master", "equipment_master", "where c_equipment_Number =? and c_do_Number=?", new Object[] {strRetEqpNo,strDoNo}, null, null, null, null);
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
                        formDataDao.delete("warrantyEquipment", "warranty_equipment",rowsWarrantyEqpData);
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
                                String strOrgDONo = eqrow.get("do_no")+"";
                                String strRepDONo = strOrgDONo.replace(strDoNo, "");
                                eqrow.put("do_no",strRepDONo);
                            }

                            formDataDao.saveOrUpdate("review_form", "review_form", rowsEqpWarrantyData);
                        }
                    }

                }


                //Add tags for DO number and SO Number in DO master table - OLDNEWDO view
                String reinvoiceQuery = "select * from apps.XXOIC_JOGETIN_OLDNEWDO_V where OLD_DO_NUMBER in ("+doNumber+") ";
                PreparedStatement reinvoicestmt = con.prepareStatement(reinvoiceQuery);
                //reinvoicestmt.setArray(1,doArray);
                //reinvoicestmt.setString(1,doNumber);
                ResultSet reinvoicers = reinvoicestmt.executeQuery();
                FormRowSet rowsDOMasterData = new FormRowSet();
                rowsDOMasterData.setMultiRow(true);

                while (reinvoicers.next()) {
                    String stroldDoNo = (reinvoicers.getString("OLD_DO_NUMBER")!= null)?reinvoicers.getString("OLD_DO_NUMBER").toString():"";
                    String strnewDoNo = (reinvoicers.getString("NEW_DO_NUMBER")!= null)?reinvoicers.getString("NEW_DO_NUMBER").toString():"";
                    String stroldSoNo = (reinvoicers.getString("OLD_SALES_ORDER")!= null)?reinvoicers.getString("OLD_SALES_ORDER").toString():"";
                    String strnewSoNo = (reinvoicers.getString("NEW_SALES_ORDER")!= null)?reinvoicers.getString("NEW_SALES_ORDER").toString():"";

                    rowsDOMasterData = formDataDao.find("do_master_form", "do_master", "where c_do_Number=?", new Object[] {stroldDoNo}, null, null, null, null);
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
                    FormRowSet invoiceNumberSet = formDataDao.find("do_master_form","do_master","where c_original_do_Number = ? ",new Object[]{stroldDoNo},null,null,null,null);
                    String invoiceNumber = "";
                    for(FormRow doRow : invoiceNumberSet){
                        invoiceNumber = doRow.get("invoiceNumber");
                        break;
                    }
                    if(rowsEqpWarrantyData != null){
                        //replace DO number
                        for(FormRow	eqrow:rowsEqpWarrantyData)
                        {
                            String doOriginal =  eqrow.get("do_no");
                            String invoices = eqrow.get("invoices");
                            LogUtil.info("Original DO",doOriginal);
                            LogUtil.info("Invoices", invoiceNumber);
                            LogUtil.info("Invoices after replace",invoices.replace(invoiceNumber+",",""));
                            //doOriginal = doOriginal.replace(stroldDoNo,strnewDoNo);
                            doOriginal = doOriginal.replace(stroldDoNo+",","");
                            eqrow.put("do_no",doOriginal);
                            eqrow.put("Invoices",invoices.replace(invoiceNumber+",",""));
                        }

                        formDataDao.saveOrUpdate("review_form", "review_form", rowsEqpWarrantyData);
                    }

                }


            }

        } catch(Exception e) {
            LogUtil.info("Exception",e.getMessage());
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


        return null;
    }
}
return EquipmentReturnCode.execute(workflowAssignment, appDef, request);
