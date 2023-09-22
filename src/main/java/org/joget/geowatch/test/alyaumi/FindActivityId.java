package org.joget.geowatch.test.alyaumi;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

import javax.servlet.http.HttpServletRequest;

public class FindActivityId {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        LogUtil.info("Running Method","Running");
        String activityId = "#assignment.activityId#";
        String processId = "#assignment.processId#";
        LogUtil.info("Process Id",processId);
        int version = Integer.parseInt(activityId.split("_")[0]);
        String nextActivityId = (version+2)+"_"+processId+"_upload_document";
        LogUtil.info("Activity Id",nextActivityId);

        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        wm.activityVariable(activityId,"activityId", nextActivityId);
        return null;
    }
}
return FindActivityId.execute(workflowAssignment, appDef, request);
