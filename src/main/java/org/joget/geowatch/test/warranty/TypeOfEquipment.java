package org.joget.geowatch.test.warranty;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeOfEquipment {
    static Connection connection = null;
    public static void getInvoiceDate(){
        String orderType = "#variable.ordertype#";
        String equipmentType = "";
        FormDataDao dao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        String id = "#variable.rId#";
        FormRow warrantyRow = dao.load("warranty_review_form","review_form",id);
        String doNumbers = warrantyRow.getProperty("do_no");
        String items = "";
        String[] doArray = doNumbers.split(",");
        // get details from the do master
        for (String doNum : doArray) {
            doNum = doNum.trim();
            FormRowSet set = dao.find("do_master_form", "do_master", "where c_do_Number =? and c_tag is null", new Object[]{doNum}, null, null, null, null);
            Map map = new HashMap();
            if (doNum != null && !doNum.isEmpty()) {
                for (FormRow row : set) {
                    String item = "'"+(String)row.get("item_code")+"',";
                    items+=item;
                }
                LogUtil.info("DO Items",items);
            }
        }
        if(items != null && items.length() > 0 && !items.isEmpty()) {
            items = items.substring(0, items.length() - 1);
        }
        try{
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
            connection = ds.getConnection();
            PreparedStatement statementSo = connection.prepareStatement("SELECT c_segment_3 FROM app_fd_items_master where c_item_code IN ("+items+")");
            ResultSet set = statementSo.executeQuery();
            Set equipSet = new HashSet();
            while(set.next()){
                equipSet.add(set.getString(1));
            }
            for(Object equipObj : equipSet){
                equipmentType += equipObj +", ";
            }
            if(equipmentType.length() > 2){
                equipmentType = equipmentType.substring(0,equipmentType.length() - 2);
            }
        }
        catch(SQLException ex){
            LogUtil.error("TypeOfEquipment",ex,ex.getMessage());
        }
        finally{
            try{
                if(connection != null && !connection.isClosed()){
                    connection.close();
                }
            }
            catch(SQLException ex){

            }
        }
        FormRowSet warrantySet = new FormRowSet();
        warrantyRow.setProperty("type_equipment",equipmentType);
        warrantySet.add(warrantyRow);
        dao.saveOrUpdate("warranty_review_form", "review_form", warrantySet);
    }
}
TypeOfEquipment.getInvoiceDate();

