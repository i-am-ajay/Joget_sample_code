import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class StoreBinder {
    static String transactionUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicmtlint/mtl_transactions_interface/";
    static String qtyUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oiconhand/query_quantities/";
    static String costUrl = "http://omuatap.oasiserp.com:8048/webservices/rest/oicitemcost/get_item_cost/";

    public static FormRowSet store(Element element, FormRowSet rows, FormData formData) throws IOException {
        rows.setMultiRow(true);
        String parentFormId = formData.getPrimaryKeyValue();
        System.out.println(parentFormId);
        // set date string
        LocalDate date = LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        for (FormRow row : rows) {
            String inventoryId = row.getProperty("INVENTORY_ITEM_ID");
            String transactionQty = row.getProperty("requiredToReserveQty");
            String transactionUom = row.getProperty("primaryUOM");
            String subInventory = row.getProperty("subInventoryId");
            String transactionCost = jsonCallForTransactionCost(inventoryId);
            String availableQty = jsonCallForQty(inventoryId,subInventory);
            double availQty = 0.0;
            double transactionQtyNum = 0.0;
            if(availableQty != null && !availableQty.trim().matches("[a-zA-Z]*")) {
                availQty = Double.parseDouble(availableQty);
            }
            if(transactionQty != null && !transactionQty.trim().matches("[a-zA-Z]*")) {
                transactionQtyNum = Double.parseDouble(transactionQty);
            }
            double result = availQty - transactionQtyNum;
            if( availQty > 0.0 && result >= 0.0 ){
                //if( availQty >= 0.0 ){
                int mainStoreStatus = jsonCallToMainStore(inventoryId, "-".concat(transactionQty), transactionCost, transactionUom, subInventory, dateStr, "#requestParam.jobOrderno#"," 2104");
                if(mainStoreStatus != 200){
                    row.setProperty("requiredToReserveQty","Main Store Inventory Not Updated. Item can't be reserved.");
                    row.setProperty("item_status","Not Reserved");
                }
                else {
                    int subInventorystatus = jsonCallToSubInventory(inventoryId, transactionQty, transactionCost, transactionUom, "J-RESERVED", dateStr, "#requestParam.jobOrderno#", "2124");
                    if(subInventorystatus != 200){
                        row.setProperty("requiredToReserveQty","Sub-Inventory not update. Kindly contanct admin to reverse Main Inventory");
                        row.setProperty("item_status","Not Reserved");
                    }
                    else{
                        row.setProperty("stockInHand",Double.toString(result));
                        row.setProperty("transactionCost",transactionCost);
                    }
                }
                row.setProperty("stockInHand",Double.toString(result));
                row.setProperty("transactionCost",transactionCost);
            }
            else{
                row.setProperty("requiredToReserveQty","Oracle QOH is "+Double.toString(availQty)+ " < Req To Reserve Qty "+transactionQty);
                row.setProperty("item_status","Not Reserved");
            }
            row.setProperty("parentFieldId", parentFormId);
        }
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        formDataDao.saveOrUpdate("allocateStockItems", "ods_allocate_mat", rows);
        return rows;
    }

    // get qty from oracle
    public static String jsonCallForQty(String inventoryId, String subInventory) throws IOException {
        URL url = new URL(qtyUrl);
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
        out.writeBytes("             \"P_INVENTORY_ITEM_ID\":\""+inventoryId+"\",\n");
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
        return parseJsonObject(content.toString(), "X_QOH");
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
        System.out.println("Response status: " + status);
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
        System.out.println("Response status: " + status);
        System.out.println(content.toString());
        return status;
    }
}
StoreBinder.store(element, rows,formData);
