package org.joget.geowatch.test.bidding;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;


public class GetEmailIds {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        FormRowSet set = new FormRowSet();
        for(Object obj : propertiesMap.keySet()){
            LogUtil.info(obj.toString(),propertiesMap.get(obj.toString()));
        }
        String msg = "<p>Dear #form.sb_vendor_user.first_name# #form.sb_vendor_user.last_name#,</p>\n" +
                "<p>You have been registered successfully.</p>\n" +
                "<p>Kindly Login with the following credentials: </p>\n" +
                "<h2> Username: #form.sb_vendor_user.new_vendor_email# </h2>\n" +
                "<h2>Your password is: #form.sb_vendor_user.user_password#</h2>\n" +
                "<h2>Below is the link to the application:</h2>\n" +
                "<a href=\"https://smartapps.alshirawi.ae/web/login\">SmartBid Application</a>\n" +
                "<p>Regards,</p>\n" +
                "<p>Oasis Group</p>";

        String projectId = "";
        Connection con = null;
        HashMap rankMap = new HashMap();
        StringBuilder builder = new StringBuilder();
        String vendorEmailIds = null;
        try {

            // retrieve connection from the default datasource
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();

            // execute SQL query
            if(!con.isClosed()) {
                String query = "SELECT c_vendor_username FROM app_fd_sb_boq_vendor WHERE c_projectId = ? and c_row_number = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setObject(1, projectId);
                stmt.setObject(2,"1");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    builder.append(rs.getString(1)).append(";");
                }
                builder.deleteCharAt(builder.length());
                vendorEmailIds = builder.toString();

                // set in workflow variable.
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
return GetEmailIds.execute(workflowAssignment, appDef, request);
