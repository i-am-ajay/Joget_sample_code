import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.joget.apps.form.model.*;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.service.*;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import javax.sql.DataSource;
import java.util.*;

public class APICall {
    static String authorization = "#envVariable.ommrestapiauthentication#";
    static String transactionUrl = "#envVariable.restAPIMRIssueQanty#";
    static String qtyUrl = "#envVariable.restAPIQOH#";
    static String costUrl = "#envVariable.restAPIGetItemCost#";

    public static FormRowSet getFormData(Element element, String primaryKey, FormData formData) throws SQLException {
        FormRowSet f = new FormRowSet();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = null;
        try {
            con = ds.getConnection();
        }
        catch(SQLException ex){
            LogUtil.error(APICall.class.getName(), ex, ex.getMessage());
        }

        if (!con.isClosed()) {
            //Get the URL parameter
            String id = primaryKey;

            //Here you can query from one or multiple tables using JOIN etc
            String sql = "SELECT c_itemNumber,c_itemDescription,c_primaryUOM,c_INVENTORY_ITEM_ID,c_subinventoryID FROM app_fd_ods_itemmaster WHERE c_itemNumber = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            if(rs == null)
                return null;

            String inventoryId = null;
            String subInventory = "RAWGEN";

            //Get value from columns of record(s)
            while (rs.next()) {
                FormRow r1 = new FormRow();
                subInventory = processColumn(rs,"c_subinventoryID");
                LogUtil.info("Subinventory",subInventory);
                r1.put("itemNumber", processColumn(rs.getString(1)));
                r1.put("itemDescription", processColumn(rs.getString(2)));
                r1.put("primaryUOM",processColumn(rs.getString(3)));
                r1.put("subinventoryID",subInventory);
                inventoryId = rs.getString(4);
                r1.put("INVENTORY_ITEM_ID", processColumn(inventoryId));
                String val = null;
                try{
                    val =  qtyOnHandJsonCall(inventoryId,subInventory);
                }
                catch(IOException ex){
                    LogUtil.error("API Call Error", ex, ex.getMessage());
                }
                if(val != null)
                    r1.put("stockInHand",val);
                f.add(r1);
            }
        }

        return f;
    }

    public static String qtyOnHandJsonCall(String itemInventoryId, String subInventory) throws IOException {
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
        String jsonString = content.toString();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        String reservedStock = processJsonArray(jsonArray, itemInventoryId);
        return reservedStock;
    }

    public static String processJsonArray(JSONArray array, String inventoryItemId){
        String quantity = "0.0";
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String jsonInventoryId = Integer.toString(obj.getInt("inventory_item_id"));
                if (inventoryItemId.equals(jsonInventoryId)) {
                    quantity = Double.toString(obj.getDouble("avai_to_trnasact_qty"));
                    break;
                }
            }
        }
        catch(Exception ex){
            //LogUtil.error(this.getClass().getName(), ex, ex.getMessage());
        }
        return quantity;
    }

    public static String processColumn(ResultSet rs, String name) throws SQLException {
        return (rs.getString(name) == null ? "" : rs.getString(name).toString().trim());
    }
    public static String processColumn(String name) throws SQLException {
        return (name == null ? "" : name);
    }
}
APICall.getFormData(element, primaryKey, formData);

