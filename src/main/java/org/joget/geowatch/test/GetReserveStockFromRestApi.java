import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetReserveStockFromRestApi {
    static String authorization = "#envVariable.ommrestapiauthentication#";
    static String jobUrl = "#envVariable.restReservedStockJobWise#";
    public static FormRowSet load(Element element, String primaryKey, FormData formData) throws IOException {
        String jobNo = "#requestParam.jobOrderno#";

        String jsonData = getReserveStock(jobNo);
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        FormRowSet set = processJsonArray(jsonArray);
        set.setMultiRow(true);
        return set;
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
            JSONObject obj = array.getJSONObject(i);
            row.setProperty("item",processColumn(obj.getString("item")));
            row.setProperty("description",processColumn(obj.getString("description")));
            row.setProperty("inventory_item_id",processColumn(obj.getString("inventory_item_id")));
            row.setProperty("job_no",processColumn(obj.getString("job_no")));
            row.setProperty("sub_inventory",processColumn(obj.getString("sub_inventory")));
            row.setProperty("transaction_uom",processColumn(obj.getString("transaction_uom")));
            row.setProperty("quantity",processColumn(obj.getString("quantity")));
            rowSet.add(row);
        }
        return rowSet;
    }
    public static String processColumn(Object name) {
        return (name == null ? "" : name.toString());
    }
}
GetReserveStockFromRestApi.load(element, primaryKey, formData);
