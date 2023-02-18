package org.joget.geowatch.test.warranty;
import java.time.LocalDate;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
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
import org.joget.workflow.model.WorkflowAssignment;

public class WarrantyItemReturnIntegration {
    static Connection con = null;
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        String var1 = "#variable.providedDate#";
        //LocalDate dateProvided = LocalDate.parse(var1);
        //LogUtil.info(dateProvided.toString(),"");
        //LogUtil.info("",var1);
        //LocalDate today = LocalDate.now();
        //LocalDate previousDay = today.minusDays(1);
        //LocalDate today = LocalDate.of(2022,7,14);
        //LocalDate previousDay = LocalDate.of(2021,7,14);
        try {
            LogUtil.info("", "Running the program-----------------------");
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
                //Populate Item Master Form
                /*String itemMasterQuery = "select * from apps.XXOIC_JOGETIN_ITEMMASTR_CAT_V where inventory_item_id in ('21700566','21844052','561559','22177170','22177178','8671472','561558','561559','561568','561569') AND "+
                 "LAST_UPDATE_DATE > to_date('2022-07-01', 'YYYY-MM-DD')";*/

                String warrantyReturnQuery = "select DO_NUMBER,ITEM_CODE,SUM(RETURNED_QTY) from apps.XXOIC_JOGETIN_DO_RETURN_ITEM WR"+
                        "GROUP BY DO_NUMBER,ITEM_CODE";
                        /*"LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";*/

                PreparedStatement itemstmt = con.prepareStatement(warrantyReturnQuery);
                //itemstmt.setString(1,previousDay.toString());
                //itemstmt.setString(2,today.toString());
                ResultSet itemrs = itemstmt.executeQuery();
                LogUtil.info("Result", "Statement executed");
                FormRowSet rowsItemMasterData = new FormRowSet();
                FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                String formIdItem = "warranty_item_return";
                String tableNameItem = "warranty_item_return";
                String fieldIDItem = "item_code";
                while (itemrs.next()) {

                    FormRow row = new FormRow();
                    //Set values
                    String itemCode = (itemrs.getString("ITEM_CODE") != null) ? itemrs.getString("ITEM_CODE").toString() : "";
                    String doNumber = (itemrs.getString("DO_NUMBER") != null) ? itemrs.getString("DO_NUMBER").toString() : "";
                    //String returnType = (itemrs.getString("RETURN_TYPE") != null) ? itemrs.getString("RETURN_TYPE").toString() : "";
                    //String returnQty = (itemrs.getString("RETURNED_QTY") != null) ? itemrs.getString("RETURNED_QTY").toString() : "";
                    //String returnReason = (itemrs.getString("RETURN_REASON") != null) ? itemrs.getString("RETURN_REASON").toString() : "";
                    //String returnDate = (itemrs.getString("RETRNED_DATE") != null) ? itemrs.getString("RETRNED_DATE").toString() : "";
                    LogUtil.info("Item Code",itemCode);
                    //Set ID - Primary Keys
                    row.setId(doNumber+""+itemCode);
                    row.setDateCreated(new Date());

                    row.setProperty("do_number", doNumber);
                    row.setProperty("item_code",itemCode);



                    //Populate the form
                    //FormRow row = formDataDao.load(formIdItem, tableName, id);
                    rowsItemMasterData.add(row);

                }
                formDataDao.saveOrUpdate(formIdItem, tableNameItem, rowsItemMasterData);
            }
            //con.close();
        }
        catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
        } finally {
            try {
                if(con != null && !con.isClosed()) {
                    con.close();
                }
            }
            catch(SQLException e) {
            }
        }
        LogUtil.info("","Project ending");
        return null;
    }
}
WarrantyItemReturnIntegration.execute(workflowAssignment, appDef, request);
