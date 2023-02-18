package org.joget.geowatch.test.einvoice;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.service.WorkflowManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetIdandReferenceColumn {
    public static Object getColumns(){
        int counter = 0;
        Connection con = null;
        String id = null;
        String compId = null;
        String uuid = null;
        // get all ids
        String idsStr = "#variable.invoices_ids#";
        String [] idsArray = null;
        if(idsStr != null && !idsStr.isEmpty()){
            idsArray = idsStr.split(",",2);

        }
        // get invoice details
        DataSource dataSource = (DataSource) AppUtil.getApplicationContext()
                .getBean("setupDataSource");

        try{
            con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement("SELECT INVH_SYS_ID,INVH_COMP_CODE,INVH_UUID FROM JOG_OT_INVOICE_HEAD WHERE INVH_SYS_ID = ?");
            pstmt.setString(1,idsArray[0]);
            ResultSet set = pstmt.executeQuery();
            while(set.next()){
                id = set.getString(1);
                compId = set.getString(2);
                uuid = set.getString(3);

                LogUtil.info("Id",id);
                LogUtil.info("Comp Id",compId);
                LogUtil.info("uuid",uuid);
            }

            // update workflow variables
            WorkflowManager wm = (WorkflowManager)AppUtil.getApplicationContext().getBean("workflowManager");
            wm.activityVariable("#assignment.activityId#", "invoice_id", idsArray[0]);
            wm.activityVariable("#assignment.activityId#", "companyId", compId);
            wm.activityVariable("#assignment.activityId#","uuid",uuid);
            if(idsArray.length == 2){
                wm.activityVariable("#assignment.activityId#","invoices_ids",idsArray[1]);
            }
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
GetIdandReferenceColumn.getColumns();
