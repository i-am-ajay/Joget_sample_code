package org.joget.geowatch.test.einvoice;

import okhttp3.*;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRow;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONException;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;

public class APITest {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef) throws IOException, JSONException {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        String url = "https://gw-apic-gov.gazt.gov.sa/e-invoicing/developer-portal/invoices/reporting/single";
        try {
            getResponseCode(url);
            //if()
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            wm.activityVariable(assignment.getActivityId(),"request_status", "false");
        }
        return null;
    }

    public static int getResponseCode(String urlString) throws IOException, IOException {
        URL u = new URL(urlString);
        HttpURLConnection huc =  (HttpURLConnection)  u.openConnection();
        huc.setRequestMethod("POST");
        huc.connect();
        return huc.getResponseCode();
    }
}
APITest.execute(workflowAssignment, appDef, request);