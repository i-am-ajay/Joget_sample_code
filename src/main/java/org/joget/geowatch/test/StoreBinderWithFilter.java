import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


public class StoreBinderWithFilter {
    public static FormRowSet store(Element element, FormRowSet rows, FormData formData) throws IOException {
        rows.setMultiRow(true);
        String parentFormId = formData.getPrimaryKeyValue();
        LogUtil.info("",parentFormId);
        // set date string
        FormRowSet set = new FormRowSet();
        for (FormRow row : rows) {
           String qty = row.getProperty("unreserve_qty");
           if(qty == null || qty.equalsIgnoreCase("") || qty.equalsIgnoreCase("0")||qty.equalsIgnoreCase("0.0")){
               continue;
           }
            row.setProperty("parentFieldId", parentFormId);
           set.add(row);
        }
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        formDataDao.saveOrUpdate("unreserveStockItemsOracle", "ods_unreserve_item", set);
        LogUtil.info("Size of list fors aving",Integer.toString(set.size()));
        return set;
    }
}
//StoreBinderWithFilter.store(element,rows,formData);
