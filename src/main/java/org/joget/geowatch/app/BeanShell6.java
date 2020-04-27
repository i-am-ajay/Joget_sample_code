package org.joget.geowatch.app;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;

public class BeanShell6 {

    public FormRowSet load(Element element, String username, FormData formData) {
        final String TAG = "TripMonitorResolveForm";

        // Constants
        String jobTypeFieldId = "jobType";
        String rmoRequiredFieldId = "rmoRequired";
        String shKey = "Self-Hauling";

        String rmoKey = "RMO";
        String rmoLabel = "Rmo";

        String haulierKey = "HAULIER";
        String haulierLabel = "Haulier";

        String requesterKey = "REQUESTER";
        String requesterLabel = "Requester";

        String jobTypeFieldValue = "#form.Trip.jobType?java#";
        String rmoRequiredFieldValue = "#form.Trip.rmoRequired?java#";

        LogUtil.info(TAG, "******************* START *******************");
        LogUtil.info(TAG, "jobTypeFieldValue: " + jobTypeFieldValue
                + ", rmoRequiredFieldValue: " + rmoRequiredFieldValue);

//        Form form = FormUtil.findRootForm(element);
//        Element jobTypeFieldElement = FormUtil.findElement(jobTypeFieldId, form, formData);
//        Element rmoRequiredFieldElement = FormUtil.findElement(rmoRequiredFieldId, form, formData);
//
//        LogUtil.info(TAG, "1");
//        if (jobTypeFieldElement == null || rmoRequiredFieldElement == null) {
//            throw new IllegalArgumentException(
//                    "form: " + form +
//                            ", jobTypeFieldElement: " + jobTypeFieldElement +
//                            ", rmoRequiredFieldElement: " + rmoRequiredFieldElement);
//        }
//
//        LogUtil.info(TAG, "2");
//        String jobTypeFieldValue = null;
//        String[] jobTypeFieldValueArray = FormUtil.getElementPropertyValues(jobTypeFieldElement, formData);
//        LogUtil.info(TAG, "2 - : " + jobTypeFieldValueArray[0]);
//
//        LogUtil.info(TAG, "2.3");
//        if (jobTypeFieldValueArray.length == 0)
//            throw new IllegalArgumentException(
//                    "jobTypeFieldValueArray: " + jobTypeFieldValueArray);
//        jobTypeFieldValue = jobTypeFieldValueArray[0];
//
//
//        LogUtil.info(TAG, "3");
//        String rmoRequiredFieldValue = null;
//        String[] rmoRequiredFieldValueArray = FormUtil.getElementPropertyValues(rmoRequiredFieldElement, formData);
//        if (rmoRequiredFieldValueArray.length == 0)
//            rmoRequiredFieldValue = rmoRequiredFieldValueArray[0];


        LogUtil.info(TAG, "5");
        FormRowSet rows = new FormRowSet();
        if ("YES".equals(rmoRequiredFieldValue)) {
            FormRow option = new FormRow();
            option.setProperty(FormUtil.PROPERTY_VALUE, rmoKey);
            option.setProperty(FormUtil.PROPERTY_LABEL, rmoLabel);
            rows.add(option);
        }

        LogUtil.info(TAG, "6");
        if (!shKey.equals(jobTypeFieldValue)) {
            FormRow option = new FormRow();
            option.setProperty(FormUtil.PROPERTY_VALUE, haulierKey);
            option.setProperty(FormUtil.PROPERTY_LABEL, haulierLabel);
            rows.add(option);
        }

        {
            LogUtil.info(TAG, "7");
            FormRow option = new FormRow();
            option.setProperty(FormUtil.PROPERTY_VALUE, requesterKey);
            option.setProperty(FormUtil.PROPERTY_LABEL, requesterLabel);
            rows.add(option);
        }

        LogUtil.info(TAG, "FINISH");
        return rows;
    }

// Call validate method with injected variable
//return validate(element, formData, values);
}
