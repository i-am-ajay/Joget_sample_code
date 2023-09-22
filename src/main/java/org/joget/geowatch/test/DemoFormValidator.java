package org.joget.geowatch.test;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.apps.form.dao.FormDataDao;


public class DemoFormValidator {
    public static boolean validate(Element element, FormData formData, String[] values) {
        boolean result = true;
            //get field 1 value from form data object
             String emailId = "new_vendor_email";
             Form form = FormUtil.findRootForm(element);
             Element emailElement = FormUtil.findElement(emailId, form, formData);

             if (emailElement != null) {
                 //get value of field 1
                 String emailValue = FormUtil.getElementPropertyValue(emailElement, formData);
                 LogUtil.info("Selected Field Value", ":" + emailValue);

                 FormDataDao formDatadao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
                 LogUtil.info("FormDataDao", "-------------------");
                 FormRowSet rows = formDatadao.find("invitation_vendor_reg", "vendor_reg", " where c_new_vendor_email=? ", new Object[]{emailValue}, null, null, null, null);
                 LogUtil.info("FormDataDao", "-------------------");
                 if (rows == null || rows.size() == 0) {
                     return true;
                 }
                 else {
                     LogUtil.info("Start", "----------");
                     FormRow row1 = rows.get(0);
                     String staatus = row1.getProperty("status");
                     if (staatus.equals("Expired")) {
                         formData.setPrimaryKeyValue(row1.getId());
                         return true;
                     }
                     else {
                         return false;
                     }
                 }
             }
         return result;
    }
}
