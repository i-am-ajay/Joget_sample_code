package org.nfpc;

import org.joget.commons.util.LogUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.*;
import org.joget.commons.util.UuidGenerator;
import org.joget.workflow.model.service.WorkflowManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StartProcess {
    public void sendMail(String [] rowKeys){
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        HashMap emailMap = new HashMap();
        String formId = "dn_invoice";
        String tableName = "invoice";
        String emailformid="create_parent_account";
        String emailtablename="create_parent_acc";
        StringBuilder builder = new StringBuilder();
        // read datalist selected values.
        for(String key : rowKeys){
            FormRow row = formDataDao.load(formId, tableName,key);
            String shipToCode = row.getProperty("ship_to_code");
            FormRowSet datalistSelected= formDataDao.find("create_parent_account", "create_parent_acc", "where c_ship_to_name like ?", new Object[] {"%"+shipToCode+"%"}, null, null, null, null);
            for(FormRow row1: datalistSelected){
                String email = row1.getProperty("username");
                if(emailMap.get(email) == null){
                    emailMap.put(row1,new ArrayList());
                }
                ((ArrayList)emailMap.get(email)).add(shipToCode);
                LogUtil.info(row1.getProperty("username"),"");
            }
            builder.append(key).append("\",");
            LogUtil.info(""+datalistSelected.size(),"");
        }
        builder.delete(builder.length()-2,builder.length());
        String idCollection = builder.toString();
        // start process for each email id
        for(Object key : emailMap.keySet()){
            ArrayList list =(ArrayList) emailMap.get(key);
            String invoices = null;
            // create invoice list.
            if(list != null && list.size() > 0){
                StringBuilder invoiceString = new StringBuilder();
                for(Object listValue : list){
                    invoiceString.append(listValue).append(", ");
                }
                invoiceString.deleteCharAt(invoiceString.length()-2);
                invoices = invoiceString.toString();
            }

            HashMap map = new HashMap();
            map.put("status","active");
            map.put("email",key.toString());
            map.put("invoices",invoices);
            map.put("recods_id",idCollection);
            UuidGenerator.getInstance().getUuid();
            workflowManager.processStart("tripImport#1#process1", null, map, null, key, false);
        }
    }
}
