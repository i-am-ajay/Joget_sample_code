package org.joget.geowatch.test;

import org.joget.apps.form.model.FormRow;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class QtyOnHandNewRestApi {
    public static String jsonCall(String itemInventoryId, String subInventory) throws IOException {
        String urlString = "http://192.168.5.216:8080/ords/ommrest/onhand_qty/v1/item_id/sub_inv_code/".concat(itemInventoryId).concat("/").concat(subInventory);
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        return content.toString();
    }

    public static String processJsonArray(JSONArray array, String inventoryItemId){
        String quantity = null;
        for(int i=0; i < array.length(); i++){
            FormRow row = new FormRow();
            JSONObject obj = array.getJSONObject(i);
            String jsonInventoryId = obj.getString("inventory_item_id");
            if(inventoryItemId.equals(jsonInventoryId)){
                quantity = obj.getString("quantity");
                break;
            }
        }
        return quantity;
    }

    public static void main(String [] args) throws IOException {
        String jsonString = jsonCall("28857957","J-RESERVED");
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        String reservedStock = processJsonArray(jsonArray, "28857957");
        System.out.println(reservedStock);
    }
}
