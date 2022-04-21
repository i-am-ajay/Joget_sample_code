package org.joget.geowatch.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.*;
import org.joget.apps.form.lib.*;
import org.joget.apps.form.service.*;
import java.sql.*;
import org.apache.commons.collections.SequencedHashMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.joget.commons.util.LogUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import java.util.HashMap;
import org.joget.apps.app.service.AppUtil;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

//new
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class GridValue
{
    public static FormRowSet saveGridRows(Element element, FormRowSet rows, FormData formData) throws SQLException, IOException, ClassNotFoundException {
        if (rows == null) {
            return null;
        }
        System.out.println("*****Enter level-2 Issue Quantity-1***********");
        String recordId = null;
        Connection con = null;

//   try {

        //Get Joget's current datasource configs
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String tableName  = "ods_MRRequestStore";
        String formDefId  = "mrRequestStore";
        String foreignKey = "fk";
        String username  = "#currentUser.username#";
        System.out.println("Enter 1 Issue Quantity-1***********");
        Form parentForm = FormUtil.findRootForm(element);
        String primaryKeyValue = parentForm.getPrimaryKeyValue(formData);
        Map exist = new HashMap();
        Date currentDate = new Date();

        Element modelFieldElement = null;
        String [] modelFieldValueArray = null;
        Form form = FormUtil.findRootForm(element);

        modelFieldElement = FormUtil.findElement("mrRefrenceNo", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String mrRefrenceNo = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            mrRefrenceNo = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("work_orderno", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String work_orderno = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            work_orderno = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("part_no", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String part_no = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            part_no = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("lbbitemCode", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String lbbitemCode = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            lbbitemCode = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("cbbitemCode", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String cbbitemCode = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            cbbitemCode = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("noofbar_lbb", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String noofbar_lbb = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            noofbar_lbb = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("noofbar_cb", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String noofbar_cb = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            noofbar_cb = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("lbb_weight", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String lbb_weight = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            lbb_weight = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("cb_weight", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String cb_weight = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            cb_weight = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("coilsItemCode", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String coilsItemCode = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            coilsItemCode = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("fg_quantityRequired", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String fg_quantityRequired = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            fg_quantityRequired = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("totallbbCount", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String totallbbCount = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            totallbbCount = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        modelFieldElement = FormUtil.findElement("totalcbCount", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String totalcbCount = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            totalcbCount = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("total_lbb_weight", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String total_lbb_weight = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            total_lbb_weight = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("total_cb_weight", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String total_cb_weight = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            total_cb_weight = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("additionalStatus", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String additionalStatus = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            additionalStatus = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("quantitytoscheduled", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String quantitytoscheduled = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            quantitytoscheduled = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("wofk", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String wofk = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            wofk = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }


        modelFieldElement = FormUtil.findElement("uom", parentForm, formData);
        modelFieldValueArray = FormUtil.getElementPropertyValues(modelFieldElement, formData);
        String uom = "";
        if(modelFieldValueArray != null && modelFieldValueArray.length > 0) {
            uom = modelFieldValueArray[0];
            modelFieldElement = null;
            modelFieldValueArray = null;
        }

        System.out.println("Enter 2 Issue Quantity-1***********");

        FormDataDao formDatadaoRow = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        con = ds.getConnection();

        FormRowSet worows = new FormRowSet();
        LocalDate date = LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        if(!con.isClosed()) {
            //To generate new record IDs for storing into child table
            UuidGenerator idGenerator=UuidGenerator.getInstance();
            FormRowSet newRecord=rows;

            Iterator i= newRecord.iterator(); // Iterating grid rows
            while (i.hasNext()) {
                FormRow formRow = (FormRow) i.next();
                /*Insert new values***/
                System.out.println("****Enter 3 Issue Quantity-1***********");

                FormRow row = new FormRow();
                String uuid = UuidGenerator.getInstance().getUuid();
                row.setDateCreated(currentDate);
                // set modified date
                row.setDateModified(currentDate);
                row.setCreatedBy(username);
                //set foreign key
                row.setProperty("id", uuid);
                row.setProperty(foreignKey, primaryKeyValue);
                row.setProperty("mrRefrenceNo", mrRefrenceNo);

                row.setProperty("work_orderno", work_orderno);
                row.setProperty("part_no", part_no);
                row.setProperty("wofk", wofk);

                row.setProperty("lbbitemCode", lbbitemCode);
                row.setProperty("cbbitemCode", cbbitemCode);
                row.setProperty("noofbar_lbb", noofbar_lbb);
                row.setProperty("noofbar_cb", noofbar_cb);


                row.setProperty("lbb_weight", lbb_weight);
                row.setProperty("cb_weight", cb_weight);
                row.setProperty("coilsItemCode", coilsItemCode);
                row.setProperty("fg_quantityRequired", fg_quantityRequired);

                row.setProperty("totallbbCount", totallbbCount);
                row.setProperty("totalcbCount", totalcbCount);
                row.setProperty("total_lbb_weight", total_lbb_weight);
                row.setProperty("total_cb_weight", total_cb_weight);


                row.setProperty("additionalStatus", additionalStatus);
                row.setProperty("quantitytoscheduled", quantitytoscheduled);
                row.setProperty("total_lbb_weight", total_lbb_weight);
                row.setProperty("total_cb_weight", total_cb_weight);
                row.setProperty("mrType", "STD");
                row.setProperty("freezeMaterialMgm","Y");
                //row.putAll(formRow);

                row.setProperty("category", processColumn(formRow.get("category")));
                row.setProperty("item_code", processColumn(formRow.get("item_code")));
                row.setProperty("subinventoryID", processColumn(formRow.get("subinventoryID")));
                row.setProperty("inventoryID", processColumn(formRow.get("inventoryID")));
                row.setProperty("availableQTY", processColumn(formRow.get("availableQTY")));
                row.setProperty("mrRequestQuantity", processColumn(formRow.get("mrRequestQuantity")));
                row.setProperty("fgCost", processColumn(formRow.get("fgCost")));
                row.setProperty("totalQuantity", processColumn(formRow.get("totalQuantity")));
                row.setProperty("totalHeatQuantity", processColumn(formRow.get("totalHeatQuantity")));
                row.setProperty("subInventory_Status", "Y");
                row.setProperty("nonuom", processColumn(formRow.get("nonuom")));
                row.setProperty("heatRefNo", processColumn(formRow.get("heatRefNo")));

                //For rest api workflow variable
                String inventoryid=formRow.get("inventoryID").toString();
                String issueqty= formRow.get("mrRequestQuantity").toString();
                String wono= work_orderno.toString();
                String subinventory=formRow.get("subinventoryID").toString();
                String itemUom=formRow.get("nonuom").toString();
                String itemCode = formRow.get("item_code").toString();

                // make call to rest api
                String result = StockIssue.deductQuantityFromOracle(issueqty,wono,itemCode,inventoryid,subinventory,itemUom,dateStr);
                row.setProperty("status",result);
                worows.add(row);

            }
        }
        formDatadaoRow.saveOrUpdate(formDefId, tableName, worows);
        con.close();
        return worows;
    }

    static class StockIssue {
        static String transactionUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicmtlint/mtl_transactions_interface/";
        static String costUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicitemcost/get_item_cost/";
        static String stockOnHand = "http://omuatap.oasiserp.com:8048/webservices/rest/oiconhand/query_quantities/";

        public static String deductQuantityFromOracle(String quantityVal, String jobId, String itemCode,
                                                      String inventoryItemId,String subInventory,String uom, String dateStr) throws ClassNotFoundException, IOException {
            String result = "Issued";
            double quantity = Double.parseDouble(quantityVal);
            // call to get reserved qty from
            String reservedStock = getReservedQtyFromOracle(jobId,itemCode);
            double reservedStockQty = 0.0;
            if (reservedStock != null && !reservedStock.trim().matches("[a-zA-Z]*")) {
                reservedStockQty = Double.parseDouble(reservedStock);
            }
            int status = 0;
            if(reservedStock == null){
                String oracleStockInHand = jsonCallForStockInHand(inventoryItemId,subInventory);
                double stockInHandQty = 0.0;
                if (oracleStockInHand != null && !oracleStockInHand.trim().matches("[a-zA-Z]*")) {
                    stockInHandQty = Double.parseDouble(oracleStockInHand);
                }
                if(stockInHandQty < quantity){
                    result = subInventory+" in hand Qty : "+stockInHandQty + " < Qty to be issued :"+quantity;
                }
                else {
                    String cost = jsonCallForTransactionCost(inventoryItemId);
                    status = jsonCallToMainStore(inventoryItemId, "-".concat(quantityVal), uom, cost, subInventory,
                            dateStr, jobId, "2125");
                    if (status != 200) {
                        result = "Failed to Issue qty from "+subInventory+" sub-inventory";
                    } else {

                    }
                }
            }
            else if(quantity > reservedStockQty){
                quantity = quantity - reservedStockQty;
                String cost = jsonCallForTransactionCost(inventoryItemId);
                status = jsonCallToSubInventory(inventoryItemId,"-".concat(reservedStock),uom,cost,"J-RESERVED",
                        dateStr,jobId,"2125");
                // deducted reserved qty and then call use unreserve stock
                if(status != 200){
                    result = "Failed to Issue qty from J-Reserved sub-inventory";
                }
                else{
                    // get stock in hand qty.
                    String oracleStockInHand = jsonCallForStockInHand(inventoryItemId,subInventory);
                    double stockInHandQty = 0.0;
                    if (oracleStockInHand != null && !oracleStockInHand.trim().matches("[a-zA-Z]*")) {
                        stockInHandQty = Double.parseDouble(oracleStockInHand);
                    }
                    if(stockInHandQty < quantity){
                        result = "Qty "+reservedStock+" issued from J-Reserve. But Failed to issue Qty"+quantity +" from "+subInventory;
                    }
                    else{
                        cost = jsonCallForTransactionCost(inventoryItemId);
                        status = jsonCallToMainStore(inventoryItemId,"-".concat(Double.toString(quantity)),uom,cost,subInventory,
                                dateStr,jobId,"2125");
                        if(status != 200){
                            result = "Failed to Issue qty from "+ subInventory +" sub-inventory";
                        }
                        else{

                        }
                    }
                }
            }
            else if(quantity <= reservedStockQty){
                String cost = jsonCallForTransactionCost(inventoryItemId);
                status = jsonCallToSubInventory(inventoryItemId,"-".concat(reservedStock),uom,cost,"J-RESERVED",
                        dateStr,jobId,"2125");
                // deducted reserved qty and then call use unreserve stock
                if(status != 200){
                    result = "Failed to Issue qty from J-Reserved sub-inventory";
                }
            }

            return result;
        }

        public static String getReservedQtyFromOracle(String jobId, String itemCode) throws ClassNotFoundException {
            String oracleQuantity = null;
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection oracleCon = null;
            try{
                oracleCon = DriverManager.getConnection("jdbc:oracle:thin:#envVariable.jdbcurlIP#", "#envVariable.jdbcuserName#", "#envVariable.jdbcPassword#");
                String query = "SELECT distinct SUM(QUANTITY),INVENTORY_ITEM_ID, SUB_INVENTORY,TRANSACTION_UOM," +
                        "JOB_NO,ITEM_CODE FROM apps.XXOIC_ORAEBS_JOGET_MMT_V where 1=1 and JOB_NO = ? AND ITEM_CODE = ?" +
                        " group by JOB_NO,ITEM_CODE,INVENTORY_ITEM_ID,SUB_INVENTORY,TRANSACTION_UOM";
                PreparedStatement stmt = oracleCon.prepareStatement(query);
                stmt.setString(1, jobId);
                stmt.setString(2,itemCode);
                ResultSet set = stmt.executeQuery();
                if(set != null && !set.isClosed()) {
                    set.next();
                    LogUtil.info("isReservedStockAvailabe",set.getString(3));
                    oracleQuantity = set.getString(1);
                }
            }
            catch(Exception ex){
                LogUtil.error("isReservedStockAvailabe",ex,ex.getMessage());
            }
            finally{
                try {
                    if (oracleCon != null && !oracleCon.isClosed()) {
                        oracleCon.close();
                    }
                }
                catch(SQLException ex){

                }
            }
            return oracleQuantity;
        }

        public static String jsonCallForStockInHand(String inventoryItemId, String subInventory) throws IOException{
            URL url = new URL("http://omuatap.oasiserp.com:8048/webservices/rest/oiconhand/query_quantities/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type", "application/json");
            con.setRequestProperty("Authorization", "Basic aXQuYW1pdG06b3JhY2xlMTIzNA==");
            /* Payload support */
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes("{\n");
            out.writeBytes("    \"QUERY_QUANTITIES_Input\": {\n");
            out.writeBytes("        \"@xmlns\": \"http://xmlns.oracle.com/apps/inv/rest/oicquantitycheck/query_quantities/\",\n");
            out.writeBytes("        \"RESTHeader\": {\n");
            out.writeBytes("            \"@xmlns\": \"http://xmlns.oracle.com/apps/fnd/rest/header\",\n");
            out.writeBytes("            \"Responsibility\": \"OMM_INVENTORY_USER\",\n");
            out.writeBytes("            \"RespApplication\": \"INV\",\n");
            out.writeBytes("            \"SecurityGroup\": \"STANDARD\",\n");
            out.writeBytes("            \"NLSLanguage\": \"AMERICAN\",\n");
            out.writeBytes("            \"Org_Id\": \"115\"\n");
            out.writeBytes("        },\n");
            out.writeBytes("        \"InputParameters\": {\n");
            out.writeBytes("            \"P_API_VERSION_NUMBER\": \"1.0\",\n");
            out.writeBytes("            \"P_INIT_MSG_LIST\": \"T\",\n");
            out.writeBytes("             \"P_ORGANIZATION_ID\":\"142\",\n");
            out.writeBytes("             \"P_INVENTORY_ITEM_ID\":\""+inventoryItemId+"\",\n");
            out.writeBytes("             \"P_ONHAND_SOURCE\": \"2\",\n");
            out.writeBytes("             \"SUBINVENTORY_CODE\":\""+subInventory+"\"  \n");
            out.writeBytes("        }\n");
            out.writeBytes("    }\n");
            out.writeBytes("}");
            out.flush();
            out.close();

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            System.out.println("Response status: " + status);
            return parseJsonObject(content.toString(),"X_QOH");
        }

        // get cost from oracle
        public static String jsonCallForTransactionCost(String inventoryId) throws IOException {
            URL url = new URL(costUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type", "application/json");
            con.setRequestProperty("Authorization", "Basic aXQudmlrYXNyYWk6b3JhY2xlMzIx");

            /* Payload support */
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes("{\n");
            out.writeBytes("    \"GET_ITEM_COST_Input\": {\n");
            out.writeBytes("        \"@xmlns\": \"http://xmlns.oracle.com/apps/bom/rest/oicitemcost/get_item_cost/\",\n");
            out.writeBytes("        \"RESTHeader\": {\n");
            out.writeBytes("            \"@xmlns\": \"http://xmlns.oracle.com/apps/fnd/rest/header\",\n");
            out.writeBytes("            \"Responsibility\": \"COST_MANAGEMENT\",\n");
            out.writeBytes("            \"RespApplication\": \"BOM\",\n");
            out.writeBytes("            \"SecurityGroup\": \"STANDARD\",\n");
            out.writeBytes("            \"NLSLanguage\": \"AMERICAN\",\n");
            out.writeBytes("            \"Org_Id\": \"908\"\n");
            out.writeBytes("        },\n");
            out.writeBytes("        \"InputParameters\": {\n");
            out.writeBytes("            \"P_API_VERSION_NUMBER\": \"1.0\",\n");
            out.writeBytes("            \"P_INVENTORY_ITEM_ID\":\"" + inventoryId + "\",\n");
            out.writeBytes("             \"P_ORGANIZATION_ID\":\"142\"\n");
            out.writeBytes("        }\n");
            out.writeBytes("    }\n");
            out.writeBytes("}");
            out.flush();
            out.close();

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return parseJsonObject(content.toString(), "GET_ITEM_COST");
        }
        // parse json output to get relevant records.
        public static String parseJsonObject(String jsonString, String reqParam) {
            try {
                JSONObject object = new JSONObject(jsonString);
                object = object.getJSONObject("OutputParameters");
                return object.get(reqParam).toString();
            } catch (Exception ex) {
                return "NaN";
            }
        }

        public static int jsonCallToMainStore(String inventoryId, String transactionQty, String transactionCost, String transactionUom, String subInventory, String dateStr, String jobNo,String transactionType){
            int status = 0;
            try {
                status = jsonCallToIssueStock(inventoryId, transactionQty, transactionCost, transactionUom,subInventory , dateStr, jobNo, transactionType);
            }
            catch(Exception ex){
                ex.getMessage();
            }
            return status;
        }

        public static int jsonCallToSubInventory(String inventoryId, String transactionQty, String transactionCost, String transactionUom, String subInventory, String dateStr, String jobNo,String transactionType) throws IOException{
            int status = 0;
            try {
                status = jsonCallToIssueStock(inventoryId, transactionQty, transactionCost, transactionUom, subInventory, dateStr, jobNo, transactionType);
            }
            catch(Exception ex){
                ex.getMessage();
            }
            return status;
        }

        public static int jsonCallToIssueStock(String inventoryId, String transactionQty, String transactionCost, String transactionUom, String subInventory, String dateStr, String jobNo,String transactionType) throws IOException{
            URL url = new URL(transactionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Basic aXQuYW1pdG06b3JhY2xlMTIzNA==");
            con.setRequestProperty("Content-Type", "application/json");

            /* Payload support */
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes("{\n");
            out.writeBytes("    \"MTL_TRANSACTIONS_INTERFACE_Input\": {\n");
            out.writeBytes("        \"@xmlns\": \"http://xmlns.oracle.com/apps/inv/concurrentprogram/rest/oicmtlint/mtl_transactions_interface\",\n");
            out.writeBytes("        \"RESTHeader\": {\n");
            out.writeBytes("            \"@xmlns\": \"http://xmlns.oracle.com/apps/fnd/rest/header\",\n");
            out.writeBytes("            \"Responsibility\": \"OMM MISCELLANEOUS USER\",\n");
            out.writeBytes("            \"RespApplication\": \"INV\",\n");
            out.writeBytes("            \"SecurityGroup\": \"STANDARD\",\n");
            out.writeBytes("            \"NLSLanguage\": \"AMERICAN\",\n");
            out.writeBytes("            \"Org_Id\": \"115\"\n");
            out.writeBytes("        },\n");
            out.writeBytes("        \"InputParameters\":{\n");
            out.writeBytes("            \"MTL_TRANSACTIONS_INTERFACE_REC\":{\n");
            out.writeBytes("            \"SOURCE_CODE\":\"Inventory\",\n");
            out.writeBytes("            \"SOURCE_LINE_ID\":2,\n");
            out.writeBytes("            \"SOURCE_HEADER_ID\":1453,\n");
            out.writeBytes("            \"PROCESS_FLAG\":1,\n");
            out.writeBytes("            \"TRANSACTION_MODE\":3,\n");
            out.writeBytes("            \"LAST_UPDATE_DATE\":\""+dateStr+"\",\n");
            out.writeBytes("            \"LAST_UPDATED_BY\":\"0\",\n");
            out.writeBytes("            \"CREATION_DATE\":\""+dateStr+"\",\n");
            out.writeBytes("            \"CREATED_BY\":\"0\",\n");
            out.writeBytes("            \"ORGANIZATION_ID\":\"142\",\n");
            out.writeBytes("            \"TRANSACTION_DATE\":\""+dateStr+"\",\n");
            out.writeBytes("            \"INVENTORY_ITEM_ID\":\""+inventoryId+"\",\n");
            out.writeBytes("            \"TRANSACTION_QUANTITY\":\""+transactionQty+"\",\n");
            out.writeBytes("            \"TRANSACTION_UOM\":\""+transactionUom+"\",\n");
            out.writeBytes("            \"TRANSACTION_TYPE_ID\":\""+transactionType+"\",\n");
            out.writeBytes("            \"SUBINVENTORY_CODE\":\""+subInventory+"\",\n");
            out.writeBytes("            \"DISTRIBUTION_ACCOUNT_ID\":\"24430\",\n");
            out.writeBytes("            \"TRANSACTION_COST\":\""+transactionCost+"\",\n");
            out.writeBytes("            \"TRANSACTION_REFERENCE\":\""+jobNo+"\",\n");
            out.writeBytes("            \"TRANSACTION_SOURCE_NAME\":\""+jobNo+"\"\n");
            out.writeBytes("            }\n");
            out.writeBytes("        }\n");
            out.writeBytes("    }\n");
            out.writeBytes("}");
            out.flush();
            out.close();

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return status;
        }
    }
    public static String processColumn(Object name) throws SQLException {
        String val = (name == null ? "" : name.toString());
        return val;
    }

}
 GridValue.saveGridRows(element, rows, formData);
