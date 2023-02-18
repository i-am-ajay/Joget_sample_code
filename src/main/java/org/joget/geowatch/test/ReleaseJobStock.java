package org.joget.geowatch.test;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReleaseJobStock {
    static String authorization = "#envVariable.ommrestapiauthentication#";
    static String transactionUrl = "#envVariable.restAPIMRIssueQanty#";
    static String qtyUrl = "#envVariable.restAPIQOH#";
    static String costUrl = "#envVariable.restAPIGetItemCost#";
    static String jobUrl = "#envVariable.restReservedStockJobWise#";

    public static void runProcess() {
        try {
            LogUtil.info(ReleaseJobStock.class.getName(),"Running Process Of stock return");
            List jobList = getJobs();
            LogUtil.info("Size",""+jobList.size());
            for(Object job : jobList){
                getJobItems(job.toString());
            }
        }
        catch(SQLException ex) {
            LogUtil.error("",ex,ex.getMessage());
        }
        catch(ClassNotFoundException ex) {
            LogUtil.error("",ex,ex.getMessage());
        } catch (IOException e) {
            LogUtil.error("",e,e.getMessage());
        }
    }
    public static List getJobs() throws SQLException{
        Connection con = getJogetConnection();
        List jobList = new ArrayList();
        if(con != null){
            String sqlJobId = "SELECT distinct c_joborderno" +
                    "    FROM app_fd_ods_joborder WHERE c_freezeStatus <> ''";
            PreparedStatement stmtJob = con.prepareStatement(sqlJobId);
            ResultSet rsJob = stmtJob.executeQuery();
            while(rsJob.next()){
                jobList.add(rsJob.getString(1));
            }
        }
        if(con!=null){
            con.close();
        }
        return jobList;
    }
    public static String releaseStock(String inventoryId, String quantity, String subInventory, String transactionUom,String jobNo){
        LocalDate date = LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        String transactionStatus = "Failure";
        try {
            String cost = jsonCallForTransactionCost(inventoryId);
            int status = jsonCallToSubInventory(inventoryId,("-").concat(quantity),cost,transactionUom,
                    "J-RESERVED",dateStr,jobNo,"2104");
            if(status == 200) {
                int mainStoreStatus = jsonCallToMainStore(inventoryId, quantity, cost, transactionUom,
                        subInventory,dateStr,jobNo,"2124");
                if(mainStoreStatus == 200) {
                    transactionStatus = "Success";
                }
                else {
                    transactionStatus = "Stock deducted from sub-inventory, but failed to reserved in main inventory. Contact Admin.";
                }
            }
            else {
                transactionStatus = "Failed to move out stock from sub-inventory. No Stock is deducted.";
            }
        }
        catch(IOException ex) {
            LogUtil.error(ReleaseJobStock.class.getName(),ex,ex.getMessage());
        }
        return transactionStatus;
    }
    public static void getJobItems(String jobId) throws ClassNotFoundException, IOException {
        String jobNo = jobId;

        String jsonData = getReserveStock(jobNo);
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        FormRowSet set = processJsonArray(jsonArray);
        set.setMultiRow(true);
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        formDataDao.saveOrUpdate("unreserveStockProcessAudit", "ods_unresv_pro_audit", set);
        LogUtil.info("","Stock Released");
    }

    public static String getReserveStock(String jobNo) throws IOException {
        URL url = new URL(jobUrl+jobNo);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", authorization);
        con.setRequestProperty("Content-Type", "application/json");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        return content.toString();
    }

    public static FormRowSet processJsonArray(JSONArray array){
        FormRowSet rowSet = new FormRowSet();
        for(int i=0; i < array.length(); i++){
            FormRow row = new FormRow();
            ;
            JSONObject obj = array.getJSONObject(i);
            String inventoryId = processColumn(obj.getString("inventory_item_id"));
            String subInventoryId = processColumn(obj.getString("sub_inventory"));
            String quantity = processColumn(obj.getString("quantity"));
            String transactionUom = processColumn(obj.getString("transaction_uom"));
            String jobId = processColumn(obj.getString("job_no"));
            String transactionStatus = releaseStock(inventoryId,subInventoryId,quantity,transactionUom,jobId);
            row.setProperty("item",processColumn(obj.getString("item")));
            row.setProperty("description",processColumn(obj.getString("description")));
            row.setProperty("inventory_item_id",inventoryId);
            row.setProperty("job_no", jobId);
            row.setProperty("sub_inventory",subInventoryId);
            row.setProperty("transaction_uom",transactionUom);
            row.setProperty("quantity",quantity);
            row.setProperty("status",processColumn(transactionStatus));
            row.setDateCreated(new Date());
            row.setDateModified(new Date());
            rowSet.add(row);
        }
        return rowSet;
    }

    public static String processColumn(Object name) {
        return (name == null ? "" : name.toString());
    }

    public static Connection getJogetConnection(){
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        try {
            con = ds.getConnection();
        }
        catch(SQLException ex){
            LogUtil.error(ReleaseJobStock.class.getName(), ex, ex.getMessage());
        }
        return con;
    }

    public static String jsonCallForTransactionCost(String inventoryId) throws IOException {
        URL url = new URL(costUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("content-type", "application/json");
        con.setRequestProperty("Authorization", authorization);

        /* Payload support */
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes("{\n");
        out.writeBytes("    \"GET_ITEM_COST_Input\": {\n");
        out.writeBytes("        \"@xmlns\": \""+costUrl+"\",\n");
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
            status = jsonCallToReserveStock(inventoryId, transactionQty, transactionCost, transactionUom,subInventory , dateStr, jobNo, transactionType);
        }
        catch(Exception ex){
            ex.getMessage();
        }
        return status;
    }

    public static int jsonCallToSubInventory(String inventoryId, String transactionQty, String transactionCost, String transactionUom, String subInventory, String dateStr, String jobNo,String transactionType) throws IOException{
        int status = 0;
        try {
            status = jsonCallToReserveStock(inventoryId, transactionQty, transactionCost, transactionUom, subInventory, dateStr, jobNo, transactionType);
        }
        catch(Exception ex){
            ex.getMessage();
        }
        return status;
    }

    public static int jsonCallToReserveStock(String inventoryId, String transactionQty, String transactionCost, String transactionUom, String subInventory, String dateStr, String jobNo,String transactionType) throws IOException{
        URL url = new URL(transactionUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", authorization);
        con.setRequestProperty("Content-Type", "application/json");

        /* Payload support */
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes("{\n");
        out.writeBytes("    \"MTL_TRANSACTIONS_INTERFACE_Input\": {\n");
        out.writeBytes("        \"@xmlns\": \""+transactionUrl+"\",\n");
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
ReleaseJobStock.runProcess();
