package org.joget.geowatch.test.warranty;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LoadWarrantySchedulerRun {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        FormDataDao dao = null;
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        HashMap map = new HashMap();
        Connection con = null;
        // fetch all draft warranties.
        DataSource dataSource = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try{
            con = dataSource.getConnection();
            String query = "SELECT wl.c_projectNumber proj_num, wl.c_hiddenprojectenddate project_endDate, wl.c_orderType order_type, wl.c_invoiceNumber invoice_number  FROM app_fd_review_form rf inner join app_fd_warranty_load wl ON rf.c_pno = wl.c_projectNumber and rf.c_warranty_status = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1,"Draft");
            ResultSet set = statement.executeQuery();

            String projectNumber = null;
            String invoiceNumber = null;
            String endDate = null;
            String type = null;

            // traverse through all the records i.e warranties in draft and run load warranty process
            while(set.next()){
                map.clear();
                projectNumber = set.getString(1) != null? set.getString(1):"";
                invoiceNumber = set.getString(4) != null? set.getString(4):"";
                endDate = set.getString(2) != null? set.getString(2):"";
                type = set.getString(3) != null? set.getString(3):"";
                map.put("projectnumber",projectNumber);
                map.put("invoicenumber",invoiceNumber);
                map.put("ordertype",type);
                map.put("projectenddate",endDate);
                workflowManager.processStart("warrantyApplication#latest#dataSyncProcess", null, map, null, UuidGenerator.getInstance().getUuid(), false);
            }
        }
        catch(SQLException ex){
            LogUtil.error(LoadWarrantySchedulerRun.class.getName(),ex,ex.getMessage());
        }
        return null;
    }
}
LoadWarrantySchedulerRun.execute(workflowAssignment, appDef, request);
