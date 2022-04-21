package org.joget.geowatch.test;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StockIssue {
    static String transactionUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicmtlint/mtl_transactions_interface/";
    static String costUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicitemcost/get_item_cost/";
    static String stockOnHand = "http://omuatap.oasiserp.com:8048/webservices/rest/oiconhand/query_quantities/";

    public FormRow store(Element element, FormRowSet set, FormData data){
        LocalDate date = LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        return null;
    }

    public int deductQuantityFromOracle(double quantity, String jobId, String itemCode,
        String inventoryItemId,String uom, String dateStr) throws ClassNotFoundException, IOException {
        // call to get reserved qty from
        String reservedStock = getReservedQtyFromOracle(jobId,itemCode);
        double reservedStockQty = 0.0;
        if (reservedStock != null && !reservedStock.trim().matches("[a-zA-Z]*")) {
            reservedStockQty = Double.parseDouble(reservedStock);
        }
        if(quantity > reservedStockQty){
            quantity = quantity - reservedStockQty;
            String cost = jsonCallForTransactionCost(inventoryItemId);
            int status = jsonCallToSubInventory(inventoryItemId,"-".concat(reservedStock),uom,cost,"J-RESERVED",
                    dateStr,jobId,"2125");
            // deducted reserved qty and then call use unreserve stock
            if(status != 200){
                // run in case of failure
            }
            else{
                // get stock in hand qty.
                String oracleStockInHand = jsonCallForStockInHand(inventoryItemId);
                double stockInHandQty = 0.0;
                if (oracleStockInHand != null && !oracleStockInHand.trim().matches("[a-zA-Z]*")) {
                    stockInHandQty = Double.parseDouble(oracleStockInHand);
                }
                if(stockInHandQty < quantity){
                    // log error that qty is less than required
                }
                else{
                   if(cost == null){
                       cost = jsonCallForTransactionCost(inventoryItemId);
                       status = jsonCallToMainStore(inventoryItemId,"-".concat(reservedStock),uom,cost,"J-RESERVED",
                               dateStr,jobId,"2125");
                       if(status != 200){
                           // log error
                       }
                       else{

                       }
                   }
                }
            }
        }
        if(quantity <= reservedStockQty){
            // dedcuted from reserved qty
        }

        return 0;
    }

    public String getReservedQtyFromOracle(String jobId, String itemCode) throws ClassNotFoundException {
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

    public String jsonCallForStockInHand(String inventoryItemId) throws IOException{
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
        out.writeBytes("             \"P_ONHAND_SOURCE\": \"2\"\n");
        out.writeBytes("\n");
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
