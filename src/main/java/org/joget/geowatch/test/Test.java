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
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONObject;

public class Test {
    public static FormRowSet load(Element element, String username, FormData formData) {
        FormRowSet rows = new FormRowSet();
        rows.setMultiRow(true);
        if (username != null && !username.isEmpty()) {
            Connection con = null;
            try {
                // retrieve connection from the default datasource
                DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
                con = ds.getConnection();

                // execut  e SQL query
                if(!con.isClosed()) {
                    PreparedStatement stmt = con.prepareStatement("SELECT id," +
                            "c_parentFieldId,c_item,c_itemDescription,c_primaryUOM,c_availableQty,c_requiredToReserveQty," +
                            "c_subInventory,c_INVENTORY_ITEM_ID,c_transactionCost, c_item_status " +
                            "FROM app_fd_ods_allocate_mat where c_parentFieldId = ?");
                    stmt.setObject(1, "#requestParam.id#");
                    System.out.println("#requestParam.id#");

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        FormRow row = new FormRow();
                        row.setProperty("id", rs.getString("id"));
                        row.setProperty("parentFieldId", (processColumn(rs,"c_parentFieldId")));
                        row.setProperty("item", (processColumn(rs,"c_item")));
                        row.setProperty("itemDescription", (processColumn(rs,"c_itemDescription")));
                        row.setProperty("primaryUOM", (processColumn(rs,"c_primaryUOM")));
                        row.setProperty("availableQty", (processColumn(rs,"c_availableQty")));
                        row.setProperty("requiredToReserveQty", (processColumn(rs,"c_requiredToReserveQty")));
                        row.setProperty("subInventory", (processColumn(rs,"c_subInventory")));
                        row.setProperty("INVENTORY_ITEM_ID", (processColumn(rs,"c_INVENTORY_ITEM_ID")));
                        row.setProperty("transactionCost", (processColumn(rs,"c_transactionCost")));
                        String itemStatus = processColumn(rs,"c_item_status");
                        row.setProperty("item_status", itemStatus);
                        rows.add(row);
                        if(itemStatus != null && itemStatus.equalsIgnoreCase("returned")){
                            System.out.println("Populating returned item");
                            FormRow newRow = addNewRow(row);
                            if(newRow == null){
                                System.out.println("Returning null");
                                FormRow dummy = new FormRow();
                                dummy.setProperty("item","Pending Stock is not available yet");
                                dummy.setProperty("enable","No");
                                rows.clear();
                                rows.add(dummy);
                                break;
                            }
                            row.setProperty("item_status","Returned (New Allocation Done)");
                            rows.add(addNewRow(row));
                        }
                    }
                }
            } catch(Exception e) {
                LogUtil.error("Sample app - Form 1", e, "Error loading user data in load binder");
            } finally {
                //always close the connection after used
                try {
                    if(con != null) {
                        con.close();
                    }
                } catch(SQLException e) {/* ignored */}
            }
        }
        System.out.println(rows.size());
        return rows;
    }
    public static String processColumn(ResultSet rs, String name) throws SQLException {
        return (rs.getString(name) == null ? "" : rs.getString(name).toString());
    }
    public static String processRowColumns(String value) {
        return value == null ? "" : value;
    }

    public static FormRow addNewRow(FormRow row) throws IOException{
        FormRow newRow = new FormRow();
        String reqQty = processRowColumns(row.getProperty("requiredToReserveQty"));
        String availQty = processRowColumns(jsonCall(row.getProperty("INVENTORY_ITEM_ID")));
        double reqVal = Double.parseDouble(reqQty.trim());
        double availVal = 0;
        System.out.println(!availQty.trim().matches("[a-zA-Z]*"));
        if(availQty != null && !availQty.trim().matches("[a-zA-Z]*")){
            System.out.println(availQty.trim().length());
            availVal = Double.parseDouble(availQty);
            if(availVal - reqVal < 0.0){
                return null;
            }
            availVal = availVal - reqVal;
        }


        newRow.setProperty("id",UuidGenerator.getInstance().getUuid());
        newRow.setProperty("parentFieldId", processRowColumns(row.getProperty("parentFieldId")));
        newRow.setProperty("item", processRowColumns(row.getProperty("item")));
        newRow.setProperty("itemDescription", processRowColumns(row.getProperty("itemDescription")));
        newRow.setProperty("primaryUOM", processRowColumns(row.getProperty("primaryUOM")));
        newRow.setProperty("availableQty", Double.toString(availVal));
        newRow.setProperty("requiredToReserveQty", processRowColumns(row.getProperty("requiredToReserveQty")));
        newRow.setProperty("subInventory", processRowColumns(row.getProperty("subInventory")));
        newRow.setProperty("INVENTORY_ITEM_ID", processRowColumns(row.getProperty("INVENTORY_ITEM_ID")));
        newRow.setProperty("transactionCost", processRowColumns(jsonCallForTransactionCost(row.getProperty("INVENTORY_ITEM_ID"))));
        newRow.setProperty("item_status", "Reserved");
        return newRow;
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
            System.out.println("Transaction Cost"+str);
            return str;
        }
        catch(Exception ex){
            return "NaN";
        }
    }

}
//return Test.load(element, primaryKey, formData);
