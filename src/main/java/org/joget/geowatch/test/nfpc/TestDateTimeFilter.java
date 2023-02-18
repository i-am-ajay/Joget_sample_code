package org.joget.geowatch.test.nfpc;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class TestDateTimeFilter {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        Connection con = null;
        try {
            // retrieve connection from the default datasource
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();

            // execute SQL query
            if(!con.isClosed()) {
                String query = "SELECT id from app_fd_rate_table where dateCreated > ?";
                PreparedStatement stmt = con.prepareStatement(query);
                LocalDateTime time = LocalDateTime.now();
                time = time.minusHours(2);
                stmt.setTimestamp(1, Timestamp.valueOf(time));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    LogUtil.info("id", rs.getString(1));
                }
            }
        } catch(Exception e) {
            LogUtil.error("Sample app - Form 1", e, "Error loading user data in load binder");
        } finally {
            //always close the connection after used
            try {
                if (con != null) {

                    con.close();
                }
            }
            catch(Exception ex){

            }
        }
        return null;
    }
}
return TestDateTimeFilter.execute(workflowAssignment, appDef, request);
