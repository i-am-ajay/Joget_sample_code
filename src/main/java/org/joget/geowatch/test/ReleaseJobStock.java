package org.joget.geowatch.test;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
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
import java.util.List;

public class ReleaseJobStock {
    static String transactionUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicmtlint/mtl_transactions_interface/";
    static String costUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicitemcost/get_item_cost/";

    public static void runProcess() {
        try {
            List jobList = getJobs();
            for(Object job : jobList){
                getJobItems(job.toString());
            }
        }
        catch(SQLException ex) {
        }
        catch(ClassNotFoundException ex) {

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
                       "Main Store",dateStr,jobNo,"2124");
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

        }
        return transactionStatus;

    }
    public static void getJobItems(String jobId) throws ClassNotFoundException{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection oracleCon = null;
        FormRowSet rowSet = new FormRowSet();
        rowSet.setMultiRow(true);
        try{
            oracleCon = DriverManager.getConnection("jdbc:oracle:thin:#envVariable.jdbcurlIP#", "#envVariable.jdbcuserName#", "#envVariable.jdbcPassword#");
            String query = "SELECT distinct QUANTITY,INVENTORY_ITEM_ID, SUB_INVENTORY,transaction_uom," +
                    "INVENTORY_ORG,JOB_NO,ITEM_CODE,TRANSACTION_TYPE,TRANSACTION_TYPE_ID FROM apps.XXOIC_ORAEBS_JOGET_MMT_V where 1=1 and JOB_NO = ?" +
                    "group by job_no,item_code";
            PreparedStatement stmt = oracleCon.prepareStatement(query);
            stmt.setString(1, jobId);
            ResultSet set = stmt.executeQuery();
            while(set.next()){
                String quantity = set.getString(1);
                String inventoryId = set.getString(2);
                String subInventoryId = set.getString(3);
                String transactionUom = set.getString(4);
                String transactionStatus = releaseStock(inventoryId,subInventoryId,quantity,transactionUom,jobId);
                String inventoryOrg = set.getString(5);
                String jobNo = set.getString(6);
                String itemCode = set.getString(7);
                String transactionType = set.getString(8);
                String transactionTypeId = set.getString(9);

                FormRow row = new FormRow();
                row.setProperty("transaction_uom",processColumn(transactionUom));
                row.setProperty("INVENTORY_ORG",processColumn(inventoryOrg));
                row.setProperty("INVENTORY_ITEM_ID",processColumn(inventoryId));
                row.setProperty("SUB_INVENTORY",processColumn(subInventoryId));
                row.setProperty("status",processColumn(transactionStatus));
                row.setProperty("QUANTITY",processColumn(quantity));
                row.setProperty("ITEM_CODE",processColumn(itemCode));
                row.setProperty("TRANSACTION_TYPE",processColumn(transactionType));
                row.setProperty("TRANSACTION_TYPE_ID",processColumn(transactionTypeId));
                row.setProperty("JOB_NO",processColumn(jobNo));
                rowSet.add(row);
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
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        formDataDao.saveOrUpdate("unreserveStockProcessAudit", "ods_unresv_pro_audit", rowSet);
    }

    public static String processColumn(String val) throws SQLException {
        return (val == null ? "" : val);
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
//ReleaseJobStock.runProcess();
