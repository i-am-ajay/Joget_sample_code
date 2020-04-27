package org.joget.geowatch.app;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;

public class BeanShell5 {

    public boolean validate0(Element element, FormData formData, String[] values) {

        // Constants
        final String rmo = "RMO";
        final String haulier = "HAULIER";
        final String monitor = "MONITOR";

        final String jobTypeField = "jobType";
        final String rmoRequiredField = "rmoRequired";
        final String approvalSubStateField = "approvalSubState";

        boolean result = false;

        Form form = FormUtil.findRootForm(element);

        Element jobTypeFieldElement = FormUtil.findElement(jobTypeField, form, formData);
        Element rmoRequiredFieldElement = FormUtil.findElement(rmoRequiredField, form, formData);
        Element approvalSubStateFieldElement = FormUtil.findElement(approvalSubStateField, form, formData);

        if (jobTypeFieldElement == null || rmoRequiredFieldElement == null || approvalSubStateFieldElement == null) {
            throw new IllegalArgumentException(
                    "jobTypeFieldElement: " + jobTypeFieldElement +
                            "rmoRequiredFieldElement: " + rmoRequiredFieldElement +
                            "approvalSubStateFieldElement: " + approvalSubStateFieldElement);
        }

        String[] rmoRequiredFieldValueArray = FormUtil.getElementPropertyValues(rmoRequiredFieldElement, formData);
        String rmoRequiredFieldValue = rmoRequiredFieldValueArray[0];

        if (rmoRequiredFieldValue != null && "YES".equals(rmoRequiredFieldValue)) {
            formData.addRequestParameterValues(approvalSubStateField, new String[]{rmo});
            result = true;
        } else {

            String[] jobTypeFieldValueArray = FormUtil.getElementPropertyValues(jobTypeFieldElement, formData);
            String jobTypeFieldValue = jobTypeFieldValueArray[0];
            if ("Self-Hauling".equals(jobTypeFieldValue)) {
                formData.addRequestParameterValues(approvalSubStateField, new String[]{monitor});
                result = true;
            } else {
                formData.addRequestParameterValues(approvalSubStateField, new String[]{haulier});
                result = true;
            }
        }

        return result;
    }

    public boolean validate(Element element, FormData formData, String[] values) {

        // Constants
        final String rmo = "RMO";
        final String haulier = "HAULIER";
        final String monitor = "MONITOR";

        final String jobTypeField = "jobType";
        final String rmoRequiredField = "rmoRequired";
        final String approvalSubStateField = "approvalSubState";

        boolean result = false;

        Form form = FormUtil.findRootForm(element);

        Element jobTypeFieldElement = FormUtil.findElement(jobTypeField, form, formData);
        Element rmoRequiredFieldElement = FormUtil.findElement(rmoRequiredField, form, formData);
        Element approvalSubStateFieldElement = FormUtil.findElement(approvalSubStateField, form, formData);

        if (jobTypeFieldElement == null || rmoRequiredFieldElement == null || approvalSubStateFieldElement == null) {
            throw new IllegalArgumentException(
                    "jobTypeFieldElement: " + jobTypeFieldElement +
                            "rmoRequiredFieldElement: " + rmoRequiredFieldElement +
                            "approvalSubStateFieldElement: " + approvalSubStateFieldElement);
        }

        String[] rmoRequiredFieldValueArray = FormUtil.getElementPropertyValues(rmoRequiredFieldElement, formData);
        String rmoRequiredFieldValue = rmoRequiredFieldValueArray[0];

        String[] jobTypeFieldValueArray = FormUtil.getElementPropertyValues(jobTypeFieldElement, formData);
        String jobTypeFieldValue = jobTypeFieldValueArray[0];

        if ("Self-Hauling".equals(jobTypeFieldValue)) {
            if ("YES".equals(rmoRequiredFieldValue)) {
                formData.addRequestParameterValues(approvalSubStateField, new String[]{rmo});
                result = true;
            } else {
                formData.addRequestParameterValues(approvalSubStateField, new String[]{monitor});
                result = true;
            }
        } else {
            formData.addRequestParameterValues(approvalSubStateField, new String[]{haulier});
            result = true;
        }

        return result;
    }
}
