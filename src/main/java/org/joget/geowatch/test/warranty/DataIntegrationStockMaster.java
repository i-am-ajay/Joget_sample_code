package org.joget.geowatch.test.warranty;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import java.sql.DriverManager;
import java.time.LocalDate;

import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;

import javax.servlet.http.HttpServletRequest;


public class DataIntegrationStockMaster {

    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate previousDay = today.minusDays(1);
        Connection con = null;
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
            if (!con.isClosed()) {

                //Populate Stock Master Form
                System.out.println("Connected");
                //String stockMasterQuery = "Select * from apps.XXOIC_JOGETIN_ITEM_COSTSTOCK_V  where inventory_item_id in ('21700566','21844052','561559','22177170','22177178','8671472','561558','561559','561568','561569')";
                String stockMasterQuery = "Select * from apps.XXOIC_JOGETIN_ITEM_COSTSTOCK_V  where LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                PreparedStatement stockstmt = con.prepareStatement(stockMasterQuery);
                stockstmt.setString(1,previousDay.toString());
                stockstmt.setString(2,today.toString());
                ResultSet stockrs = stockstmt.executeQuery();
                System.out.println("Statement executed");
                while (stockrs.next()) {
                    FormRowSet rowsStockMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");

                    //Set values - Id using Primary Key
                    row.setProperty("id", (stockrs.getString("INVENTORY_ITEM_ID") != null) ? stockrs.getString("INVENTORY_ITEM_ID").toString() : "");

                    String strStockItemCode = (stockrs.getString("ITEM_CODE") != null) ? stockrs.getString("ITEM_CODE").toString() : "";
                    row.setProperty("item_code", strStockItemCode);

                    String strAvailableStock = (stockrs.getString("ONHAND_STOCK") != null) ? stockrs.getString("ONHAND_STOCK").toString() : "";
                    row.setProperty("available_stock", strAvailableStock);

                    String strAvgCost = (stockrs.getString("AVERAGE_COST") != null) ? stockrs.getString("AVERAGE_COST").toString() : "";
                    row.setProperty("cost", strAvgCost);

                    String formIdStock = "stock_master";
                    String tableNameStock = "stock_master";
                    String fieldIDStock = "item_code";

                    //Populate the form
                    //FormRow row = formDataDao.load(formId, tableName, id);
                    rowsStockMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdStock, tableNameStock, rowsStockMasterData);
                }
            }
        }
        catch (Exception e) {
            LogUtil.error("Exception: ",e, e.getMessage());
        }
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (SQLException e) {
            }
        }
        LogUtil.info("Program","Ended");
        return null;
    }
}
return DataIntegrationStockMaster.execute(workflowAssignment, appDef, request);
