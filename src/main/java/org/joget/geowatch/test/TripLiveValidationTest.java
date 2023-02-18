package org.joget.geowatch.test;

import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;

public class TripLiveValidationTest {
    public static boolean validate(Element element, FormData formData, String[] values) {
        //get field 1 value from form data object
        FormRowSet set;
        String field1Id = "tester";
        Form form = FormUtil.findRootForm(element);
        Element field1 = FormUtil.findElement(field1Id, form, formData);
        String field1Value = FormUtil.getElementPropertyValue(field1, formData);
        System.out.println(field1Value);

        if (field1Value.equals("Live")) {
            String id = FormUtil.getElementParameterName(element);
            formData.addFormError(id, "Trip is live");
            return false;
        }
        return true;
    }
}
TripLiveValidationTest.validate(element, formData,values);
