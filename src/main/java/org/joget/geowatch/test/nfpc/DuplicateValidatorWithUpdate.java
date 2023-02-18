package org.joget.geowatch.test.nfpc;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;

public class DuplicateValidatorWithUpdate {
    public static boolean validate(Element element, FormData formData, String[] values) {
        FormDataDao dao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        Form form = FormUtil.findRootForm(element);
        String formId = "test";
        String formTable = "test";
        // get doc_no and dn file name from the submitted form.
        Element name = FormUtil.findElement("name", form, formData);
        String value = FormUtil.getElementPropertyValue(name, formData);
        LogUtil.info("Value",value);
        LogUtil.info("String Array",values[0]);
        /*Element dnFile = FormUtil.findElement("dn_file", form, formData);
        //String dnFileValue = FormUtil.getElementPropertyValue(dnFile, formData);
        //dao.find(formId, formTable, "where doc_no = ?", new Object[]{docNoValue}, null, null, null, null);
        for (FormRow row : rows) {
            String fileName = row.getProperty("dn_file");
            String recordId = row.getId();
            if (fileName != null && !fileName.isEmpty()) {
                if (fileName.equalsIgnoreCase(dnFileValue)) {
                    FormRow docRecord = dao.load(formId, formTable,recordId);
                    docRecord.setProperty("dn_file",dnFileValue);
                    FormRowSet rowSet = new FormRowSet();
                    rowSet.add(docRecord);
                    dao.saveOrUpdate(formId,formTable,rowSet);
                }
            }


            /*String projectId = "bid";
            // get root element
            Form form = FormUtil.findRootForm(element);
            Element field1 = FormUtil.findElement(projectId, form, formData);
            String fieldValue = FormUtil.getElementPropertyValue(field1, formData);


        }*/
        return false;
    }
}
DuplicateValidatorWithUpdate.validate(element, formData, values);
