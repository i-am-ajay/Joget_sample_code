// Unreserve Approval Store Binder. Checks stock in oracle at the time of approval then update accordingly.

import java.sql.*;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class StockUnreserveApprovalPostProcess {
    static String transactionUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicmtlint/mtl_transactions_interface/";
    static String qtyUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oiconhand/query_quantities/";
    static String costUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicitemcost/get_item_cost/";

    public static FormRowSet store(Element element, FormRowSet rows, FormData formData) throws IOException, ClassNotFoundException {
        rows.setMultiRow(true);
        String parentFormId = formData.getPrimaryKeyValue();
        String requestParam = formData.getRequestParameter("process_reject");
        if(requestParam != null && requestParam.equals("Reject")){
            LogUtil.info("request param", formData.getRequestParameter("process_reject"));
            for(FormRow row : rows){
                row.setProperty("status","rejected");
                row.setProperty("parentFieldId", parentFormId);
            }
        }
        else {
            // set date string
            LocalDate date = LocalDate.now();
            String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
            for (FormRow row : rows) {
                String id = row.getProperty("id");
                String inventoryId = row.getProperty("INVENTORY_ITEM_ID");
                String transactionQty = row.getProperty("approved_qty");
                String transactionUom = row.getProperty("transaction_uom");
                String permission = row.getProperty("unreserve_permission");
                String jobOrder = row.getProperty("job_no");
                String transactionId = row.getProperty("unique_transaction_id");

                if (permission.equalsIgnoreCase("allowed") && !transactionQty.equalsIgnoreCase("0.0")) {
                    String oracleQuantity = isReservedStockAvailabe(transactionId);

                    if (oracleQuantity != null) {
                        Double approveQtyNumeric = 0.0;
                        Double oracleQtyNumeric = 0.0;
                        if (transactionQty != null && !transactionQty.trim().matches("[a-zA-Z]*")) {
                            approveQtyNumeric = Double.parseDouble(transactionQty);
                        }
                        if (oracleQuantity != null && !oracleQuantity.trim().matches("[a-zA-Z]*")) {
                            LogUtil.info("Oracle Quantity String", oracleQuantity);
                            oracleQtyNumeric = Double.parseDouble(oracleQuantity);
                        }
                        double result = oracleQtyNumeric - approveQtyNumeric;
                        LogUtil.info("Oracle Numeric", Double.toString(oracleQtyNumeric));
                        LogUtil.info("Approved Numeric", Double.toString(approveQtyNumeric));
                        LogUtil.info("Result", Double.toString(result));

                        if (result >= 0.0) {

                            String transactionCost = jsonCallForTransactionCost(inventoryId);
                            LogUtil.info("", "transaction Cost");
                            int subInventorystatus = jsonCallToSubInventory(inventoryId, "-".concat(transactionQty), transactionCost, transactionUom, "J-RESERVED", dateStr, jobOrder, "2104");
                            if (subInventorystatus != 200) {
                                row.setProperty("approved_qty", "Sub-Inventory not update. Item can't be un-reserved");
                                row.setProperty("item_status", "Un-reserve Failed");
                            } else {
                                LogUtil.info("Stock Deducted from j-reserved inventory", "stock deducted");
                                int mainStoreStatus = jsonCallToMainStore(inventoryId, transactionQty, transactionCost, transactionUom, "MAIN STORE", dateStr, jobOrder, " 2124");
                                if (mainStoreStatus != 200) {
                                    row.setProperty("requiredToReserveQty", "Main Store Inventory Not Updated. Item Un-reserved from sub-inventory, contact Admin");
                                    row.setProperty("item_status", "Un-reserve Failed");
                                } else {
                                    LogUtil.info("Stock Deducted from main inventory", "stock deducted");
                                    row.setProperty("item_status", "Un-reserved");
                                }
                            }
                        } else {
                            row.setProperty("approved_qty", "Sub-Inventory Reserved Stock : " + oracleQuantity + " < Approved Unreserved Qty : " + transactionQty);
                            row.setProperty("item_status", "Un-reserve Failed");
                        }
                    }
                } else {
                    row.setProperty("item_status", "Rejected");
                }
                row.setProperty("parentFieldId", parentFormId);
            }
        }

        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        formDataDao.saveOrUpdate("unReserveItemApproval", "ods_unreserve_item", rows);
        return rows;
    }

    public static String isReservedStockAvailabe(String transactionId) throws ClassNotFoundException {
        String oracleQuantity = null;
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection oracleCon = null;
        try{
            oracleCon = DriverManager.getConnection("jdbc:oracle:thin:#envVariable.jdbcurlIP#", "#envVariable.jdbcuserName#", "#envVariable.jdbcPassword#");
            String query = "SELECT distinct JOB_NO,ITEM_CODE,QUANTITY FROM apps.XXOIC_ORAEBS_JOGET_MMT_V where 1=1 and UNIQUE_TRANSCATION_ID = ?";
            PreparedStatement stmt = oracleCon.prepareStatement(query);
            stmt.setString(1, transactionId);
            ResultSet set = stmt.executeQuery();
            if(set != null && !set.isClosed()) {
                set.next();
                LogUtil.info("isReservedStockAvailabe",set.getString(3));
                oracleQuantity = set.getString(3);
            }
            return oracleQuantity;
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
StockUnreserveApprovalPostProcess.store(element, rows, formData);