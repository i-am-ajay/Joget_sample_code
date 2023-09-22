package org.joget.geowatch.test.warranty;

import java.time.LocalDate;
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

public class DataIntegrationDO {
    static Connection con = null;

    public static void integrationForDo() throws SQLException {
        //LocalDate today = LocalDate.now();
        //LocalDate previousDay = today.minusDays(60);
        LocalDate today = LocalDate.of(2022,4,1);
        LocalDate previousDay = LocalDate.of(2022,3,1);
        LogUtil.info("Running DO Master Query","");
        try {
            //Build Url for connecting to Oracle DB
            String url = "jdbc:oracle:thin:#envVariable.jdbcURL#";
            String username = "#envVariable.jdbcUserName#";
            String password = "#envVariable.jdbcPassword#";
            //Register Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //Get Connection Using DriverManager
            con = DriverManager.getConnection(url, username, password);

            // execute SQL query
            if (!con.isClosed()) {

                //Populate DO Master Form
                System.out.println("Connected");

            /*String DOMasterQuery = "select * from apps.XXOIC_JOGETIN_DO_SHIPMENT_V " +
                        "where SO_NUMEBR IN ('SO-114030')";*/
                String DOMasterQuery = "select * from apps.XXOIC_JOGETIN_DO_SHIPMENT_V " +
                        "where LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement dostmt = con.prepareStatement(DOMasterQuery);
                dostmt.setString(1, previousDay.toString());
                dostmt.setString(2, today.toString());
                ResultSet dors = dostmt.executeQuery();
                System.out.println("Statement executed");
                int count = 0;
                int batchCount = 0;
                int batchNo = 0;
                FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                FormRowSet rowsDOMasterData = new FormRowSet();
                String formIdDO = "do_master_form";
                String tableNameDO = "do_master";
                String fieldIDDO = "do_Number";
                while (dors.next()) {
                    batchCount++;
                    count++;
                    //LogUtil.info("View",dors.getString("DO_NUMBER")+" Last Invoice Date - "+dors.getString("LAST_UPDATE_DATE"));
                    FormRow row = new FormRow();
                    //String uuid = UuidGenerator.getInstance().getUuid();

                    //Set values - Id using Primary Key
                    String strRefId = (dors.getString("REFERENCE_ID") != null) ? dors.getString("REFERENCE_ID").toString() : "";
                    row.setProperty("id", strRefId);
                    row.setProperty("reference_id", strRefId);


                    String strDONumber = (dors.getString("DO_NUMBER") != null) ? dors.getString("DO_NUMBER").toString() : "";
                    //System.out.println(strDONumber);
                    row.setProperty("do_Number", strDONumber);

                    String strDeliveryDate = (dors.getString("DELIVERY_DATE") != null) ? dors.getString("DELIVERY_DATE").toString() : "";
                    row.setProperty("delivery_date", strDeliveryDate);

                    String strSONumber = (dors.getString("SO_NUMEBR") != null) ? dors.getString("SO_NUMEBR").toString() : "";
                    row.setProperty("so_number", strSONumber);

                    String strLineNumber = (dors.getString("LINE_NUMBER") != null) ? dors.getString("LINE_NUMBER").toString() : "";
                    row.setProperty("line_number", strLineNumber);

                    String strItemcode = (dors.getString("ITEM_CODE") != null) ? dors.getString("ITEM_CODE").toString() : "";
                    row.setProperty("item_code", strItemcode);

                    String strItemDescription = (dors.getString("ITEM_DESCRIPTION") != null) ? dors.getString("ITEM_DESCRIPTION").toString() : "";
                    row.setProperty("item_desc", strItemDescription);

                    String strUOM = (dors.getString("UOM") != null) ? dors.getString("UOM").toString() : "";
                    row.setProperty("uom", strUOM);

                    String strShippedQty = (dors.getString("SHIPPED_QTY") != null) ? dors.getString("SHIPPED_QTY").toString() : "";
                    row.setProperty("shipped_qty", strShippedQty);

                    String strInvoiceNumber = (dors.getString("INVOICE_NUMBER") != null) ? dors.getString("INVOICE_NUMBER").toString() : "";
                    row.setProperty("invoiceNumber", strInvoiceNumber);

                    String strInvoiceDate = (dors.getString("INVOICE_DATE") != null) ? dors.getString("INVOICE_DATE").toString() : "";
                    row.setProperty("invoiceDate", strInvoiceDate);
                    //Populate the form
                    //FormRow row = formDataDao.load(formId, tableName, id);
                    rowsDOMasterData.add(row);
                    if(batchCount >= 100){
                        batchNo++;
                        formDataDao.saveOrUpdate(formIdDO, tableNameDO, rowsDOMasterData);
                        LogUtil.info("Batch Saved",batchNo+"");
                        rowsDOMasterData.clear();
                        batchCount = 0;
                    }

                }
                LogUtil.info("Count of Records Feb",count+"");
            }
        } catch (Exception e) {
            LogUtil.info("Exception: ",  e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        LogUtil.info("End LogUtil","---------------------------------------------");
    }
}
DataIntegrationDO.integrationForDo();
