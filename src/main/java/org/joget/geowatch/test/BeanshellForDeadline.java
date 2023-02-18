package org.joget.geowatch.test;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

import javax.servlet.http.HttpServletRequest;

public class BeanshellForDeadline {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        String id = "#process.recordId#";

        String formId = "purchaseHistory";
        String tableName= "purchase_history";

        FormDataDao dao = (FormDataDao)AppUtil.getApplicationContext().getBean("formDataDao");
        FormRow row = dao.load(formId,tableName,id);
        String val = row.getProperty("engagement_check"); // id of field from which you want to read data

        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        wm.activityVariable(assignment.getActivityId(),"engagementCheck", val);
        return null;
    }
}
BeanshellForDeadline.execute(workflowAssignment, appDef, request);
