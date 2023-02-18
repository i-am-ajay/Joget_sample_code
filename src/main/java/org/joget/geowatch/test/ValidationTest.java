package org.joget.geowatch.test;

import org.joget.apps.app.lib.EmailTool;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;


import javax.servlet.http.HttpServletRequest;

public class ValidationTest {
    public void emailMethod(){
        AppUtil
        EmailTool tool = null;
        Plugin plugin = PluginManager.getPlugin("org.joget.apps.app.lib.EmailTool");

        //Get default properties (SMTP setting) for email tool
        Map propertiesMap = AppPluginUtil.getDefaultProperties(plugin, null, appDef, null);
        propertiesMap.put("pluginManager", pluginManager);
        propertiesMap.put("appDef", appDef);
        propertiesMap.put("request", request);

        ApplicationPlugin emailTool = (ApplicationPlugin) plugin;

        String email = "ajay@mokxa.com";
        propertiesMap.put("toSpecific", email);
        propertiesMap.put("subject", "This is a test email for " + email);
        propertiesMap.put("message", "Email content for " + email);

        //set properties and execute the tool
        ((PropertyEditable) emailTool).setProperties(propertiesMap);
        emailTool.execute(propertiesMap);
    }

    public static boolean validate(Element element, FormData formData, String[] values) {
        HttpServletRequest request = null;
        //get field 1 value from form data object
        String field1Id = "equipment";
        Form form = FormUtil.findRootForm(element);
        Element field1 = FormUtil.findElement(field1Id, form, formData);
        String field1Value = FormUtil.getElementPropertyValue(field1, formData);
        System.out.println(field1Value);

        //get field 1 value from form data object
        String field2Id = "warrantyItem";
        Element field2 = FormUtil.findElement(field2Id, form, formData);
        String field2Value = FormUtil.getElementPropertyValue(field2, formData);
        System.out.println(field2Value);


        if (field1Value.equals(field2Value)) {
            String id = FormUtil.getElementParameterName(element);
            formData.addFormError(id, "Equipment and Warranty Item can not have same value!");
            return false;
        }
        return true;
    }
}
return ValidationTest.validate(element, formData, values);
