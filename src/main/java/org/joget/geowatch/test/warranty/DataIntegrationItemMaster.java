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

public class DataIntegrationItemMaster {
    static Connection con = null;
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        String var1 = "#variable.providedDate#";
        LogUtil.info("",var1);
        //LocalDate today = LocalDate.now();
        //LocalDate previousDay = today.minusDays(1);
        LocalDate today = LocalDate.of(2023,1,14);
        LocalDate previousDay = LocalDate.of(2021,1,1);
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

                String itemMasterQuery = "select * from apps.XXOIC_JOGETIN_ITEMMASTR_CAT_V where "+
                        "LAST_UPDATE_DATE >= to_date(?, 'YYYY-MM-DD') " +
                        "AND LAST_UPDATE_DATE < to_date(?,'YYYY-MM-DD')";
                LogUtil.info("Query", itemMasterQuery);
                PreparedStatement itemstmt = con.prepareStatement(itemMasterQuery);
                itemstmt.setString(1,previousDay.toString());
                itemstmt.setString(2,today.toString());
                ResultSet itemrs = itemstmt.executeQuery();
                LogUtil.info("Result", "Statement executed");
                while (itemrs.next()) {
                    FormRowSet rowsItemMasterData = new FormRowSet();
                    FormRow row = new FormRow();
                    FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                    //Set values
                    String itemMasterOrgId = (itemrs.getString("ORGANIZATION_ID") != null) ? itemrs.getString("ORGANIZATION_ID").toString() : "";
                    String itemMasterInvId = (itemrs.getString("INVENTORY_ITEM_ID") != null) ? itemrs.getString("INVENTORY_ITEM_ID").toString() : "";
                    String itemMasterId = itemMasterOrgId + "_" + itemMasterInvId;
                    //Set ID - Primary Keys
                    row.setProperty("id", itemMasterId);

                    row.setProperty("organization_id", itemMasterOrgId);
                    row.setProperty("inventory_item_id", itemMasterInvId);
                    String strItemcode = (itemrs.getString("ITEM_CODE") != null) ? itemrs.getString("ITEM_CODE").toString() : "";
                    LogUtil.info("item code",strItemcode);
                    System.out.println(strItemcode);
                    row.setProperty("item_code", strItemcode);
                    //row.setProperty("field1", "value 1");
                    String strDescription = (itemrs.getString("DESCRIPTION") != null) ? itemrs.getString("DESCRIPTION").toString() : "";
                    row.setProperty("description", strDescription);

                    String strUOM = (itemrs.getString("PRIMARY_UNIT_OF_MEASURE") != null) ? itemrs.getString("PRIMARY_UNIT_OF_MEASURE").toString() : "";
                    row.setProperty("uom", strUOM);

                    String strItemCategory = (itemrs.getString("ITEM_CATEGORY") != null) ? itemrs.getString("ITEM_CATEGORY").toString() : "";
                    row.setProperty("item_category", strItemCategory);

                    String strRegion = (itemrs.getString("REGION") != null) ? itemrs.getString("REGION").toString() : "";
                    row.setProperty("region", strRegion);

                    String segment1 = (itemrs.getString("SEGMENT1") != null) ? itemrs.getString("SEGMENT1").toString() : "";
                    row.setProperty("segment_1",segment1);

                    String segment2 = (itemrs.getString("SEGMENT2") != null) ? itemrs.getString("SEGMENT2").toString() : "";
                    row.setProperty("segment_2",segment2);

                    String segment3 = (itemrs.getString("SEGMENT3") != null) ? itemrs.getString("SEGMENT3").toString() : "";
                    row.setProperty("segment_3",segment3);

                    String formIdItem = "item_master";
                    String tableNameItem = "items_master";
                    String fieldIDItem = "item_code";

                    //Populate the form
                    //FormRow row = formDataDao.load(formIdItem, tableName, id);
                    rowsItemMasterData.add(row);
                    formDataDao.saveOrUpdate(formIdItem, tableNameItem, rowsItemMasterData);
                }
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
return DataIntegrationItemMaster.execute(workflowAssignment, appDef, request);