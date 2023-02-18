package org.joget.geowatch.test.bidding;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRow;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class UpdateRank {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        Connection con = null;
        HashMap rankMap = new HashMap();
        try {
            // retrieve connection from the default datasource
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();

            // execute SQL query
            if(!con.isClosed()) {
                String query = "SELECT id,c_vendor_username,DENSE_RANK() over( PARTITION  BY  c_item_name order by CAST(c_vendor_rate as decimal(10,2))) AS Rank FROM app_fd_sb_boq_vendor WHERE c_projectId = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setObject(1, "BID-0117");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    rankMap.put(rs.getString(1),rs.getString(3));
                    LogUtil.info("id",rs.getString(1));
                    LogUtil.info("c_vendor_username",rs.getString(2));
                    LogUtil.info("Rank",rs.getString(3));
                }
                // update rank in db.
                String updateQuery = "Update app_fd_sb_boq_vendor SET c_row_number = ? where id = ?";
                stmt = con.prepareStatement(updateQuery);
                con.setAutoCommit(false);
                for(Object key : rankMap.keySet()){
                    LogUtil.info("Id "+key.toString(),"Rank "+rankMap.get(key).toString());
                    stmt.setString(1,rankMap.get(key).toString());
                    stmt.setString(2,key.toString());
                    stmt.addBatch();
                }
                int [] count = stmt.executeBatch();
                for(int i : count){
                    LogUtil.info("I",""+i);
                }
                con.commit();
                con.setAutoCommit(true);
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
return UpdateRank.execute(workflowAssignment, appDef, request);
