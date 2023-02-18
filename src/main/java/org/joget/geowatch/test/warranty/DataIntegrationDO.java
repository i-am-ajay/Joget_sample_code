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
import org.joget.commons.util.LogUtil;

import java.sql.*;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataIntegrationDO {
    static Connection con = null;

    public static void integrationForDo() throws SQLException {
        LogUtil.info();
        LocalDate today = LocalDate.now();
        LocalDate previousDay = today.minusDays(1);
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
                        "where SO_NUMEBR IN ('SO-113510','SO-113511','SO-109457')";*/
                String DOMasterQuery = "select * from apps.XXOIC_JOGETIN_DO_SHIPMENT_V " +
                        "where LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement dostmt = con.prepareStatement(DOMasterQuery);
                dostmt.setString(1, previousDay.toString());
                dostmt.setString(2, today.toString());
                ResultSet dors = dostmt.executeQuery();
                System.out.println("Statement executed");
                while (dors.next()) {
                    FormRowSet rowsDOMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    //String uuid = UuidGenerator.getInstance().getUuid();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
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


                    String formIdDO = "do_master_form";
                    String tableNameDO = "do_master";
                    String fieldIDDO = "do_Number";

                    //Populate the form
                    //FormRow row = formDataDao.load(formId, tableName, id);
                    rowsDOMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdDO, tableNameDO, rowsDOMasterData);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }
}
DataIntegrationDO.integrationForDo();
