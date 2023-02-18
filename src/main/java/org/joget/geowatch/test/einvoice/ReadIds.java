package org.joget.geowatch.test.einvoice;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.service.WorkflowManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReadIds {
    public static Object dbCall(){
        int counter = 0;
        Connection con = null;
        StringBuilder ids = new StringBuilder();
        DataSource dataSource = (DataSource)AppUtil.getApplicationContext()
                                    .getBean("setupDataSource");

        try{
            con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement("SELECT INVH_SYS_ID FROM JOG_OT_INVOICE_HEAD ORDER BY INVH_SYS_ID LIMIT 10");
            ResultSet set = pstmt.executeQuery();
            while(set.next()){
                counter += 1;
                LogUtil.info("Counter",counter+"");
                ids.append(set.getString(1)).append(",");
                LogUtil.info("Id",set.getString(1));
            }
            String invoiceIds = "";
            if(ids.length() > 0) {
                ids.deleteCharAt(ids.length() - 1);
                invoiceIds = ids.toString();
            }
            LogUtil.info("Invoice Ids",invoiceIds);
            WorkflowManager wm = (WorkflowManager)AppUtil.getApplicationContext().getBean("workflowManager");
            wm.activityVariable("#assignment.activityId#", "invoices_ids", invoiceIds);
            //wm.activityVariable("#assignment.activityId#", "counter",counter+"");
            wm.activityVariable("#assignment.activityId#", "counter","0");
        }
        catch(SQLException ex){
            LogUtil.error("ReadIds",ex,ex.getMessage());
        }
        finally{
            try {
                con.close();
            }
            catch(SQLException ex){

            }
        }
        return null;
    }
}
ReadIds.dbCall();
