package org.joget.geowatch.test;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRowSet;

import java.io.IOException;

public class StoreBinderForSoftAllocationAction {
    public static FormRowSet store(Element element, FormRowSet rows, FormData formData) throws IOException {
        rows.setMultiRow(true);
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        formDataDao.saveOrUpdate("maSoftAllocationDetails", "ods_soft_allc_det1", rows);
        return rows;
    }
}
StoreBinderForSoftAllocationAction.store(element, rows, formData);
