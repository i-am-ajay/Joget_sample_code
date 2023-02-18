package org.joget.geowatch.test.warranty;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;

import java.util.Date;

public class UpdateWarrantyDate {
    public static void updateDates(){
        String id = "#variable.rId#";
        String processType = "#variable.processType#";
        String formId = "warranty_review_form";
        String tableName = "review_form";
        FormDataDao dao = (FormDataDao)AppUtil.getApplicationContext().getBean("formDataDao");
        FormRow warrantyRow = dao.load(formId,tableName,id);
        if(processType.equals("Reload")){
            warrantyRow.setDateModified(new Date());
        }
        else{
            warrantyRow.setDateCreated(new Date());
            warrantyRow.setDateModified(new Date());
        }
        FormRowSet rowSet = new FormRowSet();
        dao.saveOrUpdate(formId,tableName,rowSet);
    }
}
UpdateWarrantyDate.updateDates();