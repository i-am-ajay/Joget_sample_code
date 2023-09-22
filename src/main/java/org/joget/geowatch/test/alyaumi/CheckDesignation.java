package org.joget.geowatch.test.alyaumi;

import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowManager;

public class CheckDesignation {
    public static void getDesignation(){
        String designation = "#variable.designation#";
        isGm(designation);
    }
    public static void isGm(String designation){
            designation = designation.toLowerCase();
            if(designation.contains(" gm ") || designation.contains("general manager")){
                PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
                WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
                wm.activityVariable("#assignment.activityId#","designation", "GM");
            }
    }
}
CheckDesignation.getDesignation();
