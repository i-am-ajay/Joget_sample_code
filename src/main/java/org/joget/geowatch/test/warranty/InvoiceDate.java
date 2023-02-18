package org.joget.geowatch.test.warranty;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class InvoiceDate {
    public static void getInvoiceDate(){
        FormDataDao dao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        String id = "#variable.rId#";
        FormRow warrantyRow = dao.load("warranty_review_form","review_form",id);
        String doNumbers = warrantyRow.getProperty("do_no");
        String[] doArray = doNumbers.split(",");
        LogUtil.info("Warranty Id", id);
        // get warranty record.

        String mainInvoiceField = "";
        String mainInvoiceDetailsField = "";
        // get details from the do master
        for(String doNum : doArray){
            LogUtil.info("Looping through DO numbers",doNum);
            doNum = doNum.trim();
            Map map = new HashMap();
            if(doNum != null && !doNum.isEmpty()) {
                FormRowSet set = dao.find("do_master_form", "do_master", "where c_do_Number =? and c_tag is null", new Object[]{doNum}, null, null, null, null);
                for (FormRow row : set) {
                    LogUtil.info("Invoice Number", row.get("invoiceNumber").toString());
                    map.put(row.get("invoiceNumber"), row.get("invoiceDate"));
                }
            }
            String invoceDetails = "";
            String invoice = "";
            for(Object key : map.keySet()){
                String date = (String)map.get(key);
                if(date != null && !date.isEmpty() && date.length() > 10){
                    date = date.substring(0,10);
                }
                invoceDetails = ""+key+"( "+date+" ), ";
                invoice += ""+key+", ";
            }
            mainInvoiceField += invoice;
            mainInvoiceDetailsField += invoceDetails;
        }
        if(mainInvoiceField != null && !mainInvoiceField.isEmpty()) {
            if(mainInvoiceField.trim().length() > 2) {
                LogUtil.info("Invoices", mainInvoiceField.substring(0, mainInvoiceField.length() - 2));
                warrantyRow.setProperty("invoices", mainInvoiceField.substring(0, mainInvoiceField.length() - 2));
            }
        }
        else{
            warrantyRow.setProperty("invoices", mainInvoiceField);
        }

        if(mainInvoiceDetailsField != null && !mainInvoiceDetailsField.isEmpty()) {
            if(mainInvoiceDetailsField.trim().length() > 2) {
                LogUtil.info("Invoices Details", mainInvoiceDetailsField.substring(0, mainInvoiceDetailsField.length() - 2));
                warrantyRow.setProperty("invoice_for_report", mainInvoiceDetailsField.substring(0, mainInvoiceDetailsField.length() - 2));
            }
        }
        else{
            warrantyRow.setProperty("invoice_for_report",mainInvoiceDetailsField);
        }
        FormRowSet warrantySet = new FormRowSet();
        warrantySet.add(warrantyRow);
        dao.saveOrUpdate("warranty_review_form","review_form",warrantySet);
    }
}
InvoiceDate.getInvoiceDate();
