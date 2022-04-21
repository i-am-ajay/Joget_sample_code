import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

class TransactionAPI{
    public static Object execute(AppDefinition appDef, HttpServletRequest request) throws SQLException{
        // set date string
        LocalDate date = LocalDate.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

        String jobOrder = request.getParameter("jobOrderno");
        Enumeration en = request.getParameterNames();
        while(en.hasMoreElements()){
            System.out.println(en.nextElement()+" -> "+request.getParameter(en.nextElement().toString()));
        }
        String id = null;

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        try {
            con = ds.getConnection();
        }
        catch(SQLException ex){
            LogUtil.error(TransactionAPI.class.getName(), ex, ex.getMessage());
        }

        if (!con.isClosed()) {
            // get job order id
            String sqlJobId = "SELECT id FROM app_fd_ods_joborder_all WHERE c_jobOrderno = ?";
            PreparedStatement stmtJob = con.prepareStatement(sqlJobId);
            stmtJob.setString(1,jobOrder);
            ResultSet rsJob = stmtJob.executeQuery();
            if(rsJob != null){
                rsJob.next();
                id = rsJob.getString(1);
            }
            //Here you can query from one or multiple tables using JOIN etc
            String sql = "SELECT c_INVENTORY_ITEM_ID,c_qtyToReturn,c_transactionCost,c_primaryUOM,c_subInventory,id FROM app_fd_ods_allocate_mat WHERE c_parentFieldId = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            List idList = new ArrayList();
            if(rs == null)
                return null;
            String inventoryId = null;
            String transactionCost = null;
            String transactionQty = null;
            String transactionUom = null;
            String subInventory = null;

            //Get value from columns of record(s)
            while (rs.next()) {
                inventoryId = rs.getString(1);
                transactionQty = rs.getString(2);
                transactionCost = rs.getString(3);
                transactionUom = rs.getString(4);
                subInventory = rs.getString(5);
                id = rs.getString(6);
                System.out.println("id");
                idList.add(id);

                try{
                    jsonCall(inventoryId, transactionQty, transactionCost, transactionUom,subInventory, dateStr);
                    System.out.println("Success : 200");
                }
                catch(IOException ex){
                    LogUtil.error("API Call Error", ex, ex.getMessage());
                }
            }
            for(Object recordId : idList) {
                System.out.println(recordId.toString());
                String recrodIdString = (String)recordId;
                //SELECT c_INVENTORY_ITEM_ID,c_qtyToReturn,c_transactionCost,c_primaryUOM,c_subInventory,id FROM app_fd_ods_allocate_mat WHERE c_parentFieldId = ?";
                String updateRecord = "Update app_fd_ods_allocate_mat Set c_qtyToReturn=? WHERE id = ?";
                PreparedStatement udpateRecord = con.prepareStatement(updateRecord);
                udpateRecord.setString(1, "Qty Not Updated");
                udpateRecord.setString(2,recrodIdString);
                udpateRecord.executeUpdate();
            }
        }
        return null;

    }

    public static void jsonCall(String inventoryId, String transactionQty, String transactionCost, String transactionUom, String subInventory, String dateStr) throws IOException {
        URL url = new URL("http://omuatap.oasiserp.com:8048/webservices/rest/oicmtlint/mtl_transactions_interface/");
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
        out.writeBytes("            \"LAST_UPDATE_DATE\":\" "+dateStr+"\",\n");
        out.writeBytes("            \"LAST_UPDATED_BY\":\"0\",\n");
        out.writeBytes("            \"CREATION_DATE\":\""+dateStr+"\",\n");
        out.writeBytes("            \"CREATED_BY\":\"0\",\n");
        out.writeBytes("            \"ORGANIZATION_ID\":\"142\",\n");
        out.writeBytes("            \"TRANSACTION_DATE\":\""+dateStr+"\",\n");
        out.writeBytes("            \"INVENTORY_ITEM_ID\":\""+inventoryId+"\",\n");
        out.writeBytes("            \"TRANSACTION_QUANTITY\":\"-"+transactionQty+"\",\n");
        out.writeBytes("            \"TRANSACTION_UOM\":\""+transactionUom+"\",\n");
        out.writeBytes("            \"TRANSACTION_TYPE_ID\":\"2025\",\n");
        out.writeBytes("            \"SUBINVENTORY_CODE\":\""+subInventory+"\",\n");
        out.writeBytes("            \"DISTRIBUTION_ACCOUNT_ID\":\"24430\",\n");
        out.writeBytes("            \"TRANSACTION_COST\":\""+transactionCost+"\"\n");
        out.writeBytes("           }\n");
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
    }

    public static String jsonCall(String inventoryId) throws IOException {
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
        out.writeBytes("             \"P_INVENTORY_ITEM_ID\":\""+inventoryId+"\",\n");
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


    public static String parseJsonObject(String jsonString, String reqParam){
        try {
            JSONObject object = new JSONObject(jsonString);
            object = object.getJSONObject("OutputParameters");
            return object.get(reqParam).toString();
        }
        catch(Exception ex){
            return "NaN";
        }
    }

    public static String jsonCallForTransactionCost(String inventoryId) throws IOException {
        URL url = new URL("http://omuatap.oasiserp.com:8048/webservices/rest/oicitemcost/get_item_cost/");
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
        out.writeBytes("            \"P_INVENTORY_ITEM_ID\":\""+inventoryId+"\",\n");
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
        return parseJsonObjectForTransactionCost(content.toString(),"GET_ITEM_COST");
    }


    public static String parseJsonObjectForTransactionCost(String jsonString, String reqParam){
        try {
            JSONObject object = new JSONObject(jsonString);
            object = object.getJSONObject("OutputParameters");
            String str = object.get(reqParam).toString();
            return str;
        }
        catch(Exception ex){
            return "NaN";
        }
    }
}
//TransactionAPI.execute(appDef,request);