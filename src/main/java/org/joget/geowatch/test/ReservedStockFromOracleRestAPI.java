package org.joget.geowatch.test;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class ReservedStockFromOracleRestAPI {
    static String authorization = "#envVariable.ommrestapiauthentication#";
    static Connection jogetConnection = null;
    static PreparedStatement prepStatement = null;

    public static FormRowSet load(Element element, String primaryKey, FormData formData) throws IOException {
        String jobNo = "#requestParam.jobOrder#";
        String itemNumber = "#requestParam.itemNumber#";

        String jsonData = getReserveStock(jobNo, itemNumber);
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        FormRowSet set = processJsonArray(jsonArray);
        set.setMultiRow(true);
        LogUtil.info("set", "" + set.size());
        return set;
    }

    public static String getReserveStock(String jobNo, String inventoryItemId) throws IOException {
        String jobUrl = "http://192.168.5.216:8080/ords/ommrest/ommjob/v1/Job_No/";
        String itemUrl = "http://192.168.5.216:8080/ords/ommrest/ommjob/v1/inventory_item_id/";
        URL url = null;
        if (jobNo != null && !jobNo.equals("")) {
            url = new URL(jobUrl.concat(jobNo));
        } else {
            url = new URL(itemUrl.concat(inventoryItemId));
        }

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
        LogUtil.info("Response status: ", Integer.toString(status));
        return content.toString();
    }

    public static FormRowSet processJsonArray(JSONArray array) {
        FormRowSet rowSet = new FormRowSet();
        for (int i = 0; i < array.length(); i++) {
            FormRow row = new FormRow();
            JSONObject obj = array.getJSONObject(i);
            String inventoryItemId = processColumn(obj.getString("inventory_item_id"));
            LogUtil.info("inventory_item_id", processColumn(obj.getString("inventory_item_id")));
            String subInventory = getItemSubInventory(processColumn(obj.getString("inventory_item_id")));
            row.setProperty("item_code",processColumn(obj.getString("item")));
            row.setProperty("item_description",processColumn(obj.getString("description")));
            row.setProperty("INVENTORY_ITEM_ID", processColumn(obj.getString("inventory_item_id")));
            row.setProperty("job_no", processColumn(obj.getString("job_no")));
            row.setProperty("sub_inventory", getItemSubInventory(processColumn(obj.getString("inventory_item_id"))));
            row.setProperty("transaction_uom", processColumn(obj.getString("transaction_uom")));
            row.setProperty("quantity", processColumn(obj.getString("quantity")));
            LogUtil.info("INVENTORY_ITEM_ID", inventoryItemId);
            rowSet.add(row);
        }
        return rowSet;
    }

    public static String getItemSubInventory(String inventoryItemId){
        LogUtil.info("Inventory Item id",inventoryItemId);
        String subInventory = "";
        try {
            PreparedStatement ps = getPrepStatement();
            ps.setString(1,inventoryItemId);
            ResultSet set = ps.executeQuery();
            if(set != null && !set.isClosed()){
                set.next();
                subInventory = set.getString(5);
                LogUtil.info("Sub Inventory",subInventory);
            }
        }
        catch(SQLException ex){
            LogUtil.error(ReservedStockFromOracleRestAPI.class.getName(),ex, ex.getMessage());
        }
        return subInventory;
    }

    public static PreparedStatement getPrepStatement() throws SQLException {
        if(jogetConnection == null ){
            getConnection();
        }
        if(prepStatement == null){
            String query = "SELECT c_itemNumber,c_itemDescription,c_primaryUOM,c_INVENTORY_ITEM_ID,c_subinventoryID " +
                "FROM app_fd_ods_itemmaster WHERE c_INVENTORY_ITEM_ID = ?";
            prepStatement = jogetConnection.prepareStatement(query);
        }
        return prepStatement;
    }

    public static void getConnection(){
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
            jogetConnection = ds.getConnection();
        }
        catch(SQLException ex){
            LogUtil.error(ReservedStockFromOracleRestAPI.class.getName(), ex, ex.getMessage());
        }
    }

    public static String processColumn(Object name) {
        return (name == null ? "" : name.toString());
    }
}

ReservedStockFromOracleRestAPI.load(element,primaryKey,formData);
