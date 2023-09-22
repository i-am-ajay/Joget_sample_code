package org.joget.geowatch.test.alyaumi;

import org.joget.apps.app.service.AppUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowManager;

public class GetApprovalPerson {
    PluginManager pluginManager;
    WorkflowManager wm;
    public Object checkApproval(){
        String user = "#variable.assignment_user#";
        String userOrganization = "#user."+user+".organization.name#";
        // pass group name in the method call.
        getApprover("sales",userOrganization,"");
        return null;
    }

    public String getApprover(String group, String organization, String workflowVariableName){
        if(pluginManager == null) {
            pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        }
        if(wm == null) {
            wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        }
        String users = "#user.group."+group+".users";
        String[] userArray = users.split(";");
        for(String userStr : userArray){
            String org = "#user."+userStr+".organization.name#";
            if(organization.equals(org)){
                wm.activityVariable("#assignment.activityId#",workflowVariableName, userStr);
                return userStr;
            }
        }
    }
}
