package org.joget.geowatch.test.nfpc;

import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;

public class DuplicateValidationBeanshell {
    public static boolean validate(Element element,  FormData formData) {
        Form form = FormUtil.findRootForm(element);
        Element field1 = FormUtil.findElement("first_name", form, formData);
        String value = FormUtil.getElementPropertyValue(field1,formData);
        LogUtil.info("Value",value);


        Element field2 = FormUtil.findElement("id",form,formData);
        String value2 = FormUtil.getElementPropertyValue(field2,formData);
        LogUtil.info("Id",value2);
        /*for(FormRow row : rows){
            row.getProperty("first_name");
            row.getProperty("second_name");
            row.getProperty("contact");
        }*/
        return false;
    }
}
DuplicateValidationBeanshell.validate(element,formData);
