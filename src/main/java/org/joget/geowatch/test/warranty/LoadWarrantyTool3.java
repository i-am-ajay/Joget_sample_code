package org.joget.geowatch.test.warranty;

import org.joget.apps.app.model.AppDefinition;
import org.joget.workflow.model.WorkflowAssignment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.HashMap;
import java.sql.DriverManager;
import java.util.*;
import java.lang.*;
import java.util.Formatter;
import org.apache.commons.lang.StringUtils;
import java.lang.Object;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.*;
import org.joget.apps.form.dao.FormDataDao;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import org.joget.commons.util.UuidGenerator;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowManager;


import javax.servlet.http.HttpServletRequest;

public class LoadWarrantyTool3 {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        Map item_sum = new HashMap();
        Map item_cat = new HashMap();
        Map item_categry = new HashMap();
        Map returnItemMap = new HashMap();
        String strdo_no = "";
        FormRowSet rows = new FormRowSet();
        rows.setMultiRow(true);
        String curr_id = "#process.recordId#";
        String processType = "#variable.processType#";

        Connection con = null;
        Connection oracleConnection = null;
        LogUtil.info("Current Id",curr_id);
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        try {
            LogUtil.info("Record Id","#variable.rId#");
            if(processType.equals("Reload")){
                curr_id = "#variable.rId#";
                LogUtil.info("Record Id","#variable.rId#");
            }

            // set the warranty id in rid.
            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
            wm.activityVariable(assignment.getActivityId(),"rId", curr_id);
            // retrieve connection from the default datasource
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();


            FormRow warranty = formDataDao.load("warranty_review_form", "review_form", curr_id);
            LogUtil.info("--- 3rd Bean Shell Tool Running for Warranty Certificate Table ", " ------- \n");
            if(warranty != null ){
                LogUtil.info("--- If loop ", " ------- \n"+ curr_id);

                strdo_no = (warranty.get("do_no")!= null)?warranty.get("do_no").toString():"";

                LogUtil.info("--- DO numbers ", " ------- \n"+strdo_no);

                String[] DO_Nums = strdo_no.split(",");
                ArrayList DOs = new ArrayList(Arrays.asList(DO_Nums));

                String url = "jdbc:oracle:thin:#envVariable.jdbcURL#";
                String username = "#envVariable.jdbcUserName#";
                String password = "#envVariable.jdbcPassword#";


                for(Object strObj : DOs){
                    String strDO = strObj.toString();
                    returnItemMap.clear();
                    // get return information
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    oracleConnection = DriverManager.getConnection(url, username, password);
                    if(!oracleConnection.isClosed() ){
                        LogUtil.info("Oracle DO Number",strDO);
                        String returnQuery = "select DO_NUMBER,ITEM_CODE,SUM(RETURNED_QTY) return_qty from apps.XXOIC_JOGETIN_DO_RETURN_ITEM WHERE DO_NUMBER = ? GROUP BY DO_NUMBER,ITEM_CODE";
                        PreparedStatement returnStmt = oracleConnection.prepareStatement(returnQuery);
                        returnStmt.setString(1,strDO.trim());
                        ResultSet returnSet = returnStmt.executeQuery();
                        while(returnSet.next()){
                            LogUtil.info("Oracle loop","Running");
                            LogUtil.info("Item Code",returnSet.getString(2));
                            LogUtil.info("qty",returnSet.getString(3));
                            returnItemMap.put(returnSet.getString(2),returnSet.getString(3));
                        }
                        returnStmt.close();
                    }

                    LogUtil.info("--- DO number ", " ------- \n"+strDO);
                    strDO = strDO.replaceFirst("^\\s*", "");
                    // execute SQL query
                    if(!con.isClosed()) {
                        // get returned item details


                        String stmt = "SELECT d.c_item_code,i.c_description, d.c_shipped_qty, i.c_item_category FROM app_fd_items_master AS i inner join app_fd_do_master AS d ON i.c_item_code=d.c_item_code"
                                +" where d.c_do_Number='"+strDO+"'"
                                +" GROUP BY d.c_item_code, i.c_item_category,i.c_description, d.c_shipped_qty";


                        LogUtil.info("--- Query ", " ------- \n"+stmt);
                        //stmt.setObject(1, );
                        Statement statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(stmt);
                        while (rs.next()) {
                            String stritemCode = (rs.getObject("c_item_code") != null)?rs.getObject("c_item_code").toString():"";
                            String strShippedQty = (rs.getObject("c_shipped_qty") != null)?rs.getObject("c_shipped_qty").toString():"";
                            String stritemCategory = (rs.getObject("c_item_category") != null)?rs.getObject("c_item_category").toString():"";
                            String stritemdesc = (rs.getObject("c_description") != null)?rs.getObject("c_description").toString():"";
                            int iShipQty = Integer.parseInt(strShippedQty);
                            /*int rShipQty = 0;
                            LogUtil.info("String Item Code",stritemCode);
                            rShipQty = (returnItemMap.get(stritemCode)==null || returnItemMap.get(stritemCode).equals(""))?0:Integer.parseInt(returnItemMap.get(stritemCode).toString());
                            LogUtil.info("Return Qty",""+rShipQty);
                            iShipQty = iShipQty - rShipQty;*/
                            LogUtil.info("Ship Qty",""+iShipQty);
                            LogUtil.info("--- Item Code ", " ------- \n"+stritemCode);
                            LogUtil.info("--- ShippedQty ", " ------- \n"+iShipQty);

                            if(item_sum.containsKey(stritemCode))
                            {
                                item_sum.put(stritemCode, (int)item_sum.get(stritemCode)+iShipQty);
                            }
                            else
                            {
                                item_sum.put(stritemCode, iShipQty);
                            }

                            if(!item_cat.containsKey(stritemCode)){
                                item_cat.put(stritemCode, stritemdesc);
                            }

                            if(!item_categry.containsKey(stritemCode)){
                                item_categry.put(stritemCode, stritemCategory);
                            }

                        }

                    }

                }
                LogUtil.info("Return Part Executed","Return part over");

            }


        } catch(Exception e) {
            LogUtil.error("Tool 3 - warranty load program", e, "Error loading user data in load binder");
        } finally {
            //always close the connection after used
            try {
                if(con != null) {
                    con.close();
                }
                if(oracleConnection != null){
                    oracleConnection.close();
                }
            } catch(SQLException e) {/* ignored */}
        }

        if(processType.equals("Reload")){
            String formId = "certificate_equipment_details";
            String tableName = "equipment_details";
            FormRowSet warrantyItemSet = formDataDao.find(formId,tableName,"where c_warranty_ref = ?", new Object[]{curr_id},null,false,null,null);
            formDataDao.delete(formId,tableName,warrantyItemSet);
        }

        FormRowSet starndardWarrantySet = formDataDao.find("warrantyValidity","warranty",null,null,null,null,null,null);
        HashSet equipmentSet = new HashSet();
        for(FormRow row : starndardWarrantySet){
            equipmentSet.add(row.getProperty("equipment"));
        }
        boolean itemFlag = false;
        boolean equipmentFlag = false;
        String items = "";
        for (Object key: item_sum.keySet()) {
            FormRow row = new FormRow();
            // Reduce return qty from total qty
            int rShipQty = (returnItemMap.get(key)==null || returnItemMap.get(key).equals(""))?0:Integer.parseInt(returnItemMap.get(key).toString());
            int iQty = (int)item_sum.get(key);
            iQty = iQty - rShipQty;
            LogUtil.info("Item Qty",iQty+"");
            LogUtil.info("Returned Qty",rShipQty +"");
            String newitemid = UuidGenerator.getInstance().getUuid();
            if(!equipmentSet.contains(item_categry.get(key))){
                continue;
            }
            String item = "'"+(String)key+"',";
            items+=item;
            equipmentFlag = true;
            row.setProperty("id", newitemid);
            row.setProperty("itemCode", key.toString());
            row.setProperty("shippedQty", iQty+"");
            row.setProperty("itemCategory", item_categry.get(key)+"");
            row.setProperty("itemDescription", item_cat.get(key)+"");
            row.setProperty("warranty_ref", curr_id);
            rows.add(row);
            LogUtil.info("Item code: "+key,"Qty: "+iQty+"");
        }
        if(!equipmentFlag){
            FormRow row = new FormRow();
            row.setProperty("id",UuidGenerator.getInstance().getUuid());
            row.setProperty("itemDescription","This warranty has no item to return");
            row.setProperty("warranty_ref", curr_id);
            rows.add(row);
        }
        else{
            if(items != null && !items.isEmpty() && items.length() > 1){
                items = items.substring(0, items.length() - 1);
                LogUtil.info("---------Item for equipment",items);
                updateEquipmentType(items);
            }
        }
        formDataDao.saveOrUpdate("certificate_equipment_details", "equipment_details", rows);
        return null;
    }

    public static void updateEquipmentType(String items){
        Connection connection = null;
        String equipmentType = "";
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
        String id = "#variable.rId#";
        FormDataDao dao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        FormRow warrantyRow = dao.load("warranty_review_form","review_form",id);
        FormRowSet warrantySet = new FormRowSet();
        warrantyRow.setProperty("type_equipment",equipmentType);
        warrantySet.add(warrantyRow);
        dao.saveOrUpdate("warranty_review_form", "review_form", warrantySet);
    }
}
return LoadWarrantyTool3.execute(workflowAssignment, appDef, request);
