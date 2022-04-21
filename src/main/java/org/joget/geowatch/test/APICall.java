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
import org.json.JSONObject;

import java.sql.*;
import javax.sql.DataSource;
import java.util.*;

public class APICall {
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
            String sql = "SELECT c_itemNumber,c_itemDescription,c_primaryUOM,c_INVENTORY_ITEM_ID FROM app_fd_ods_itemmaster WHERE c_itemNumber = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            if(rs == null)
                return null;

            String inventoryId = null;
            String subInventory = "RAW-C";

            //Get value from columns of record(s)
            while (rs.next()) {
                FormRow r1 = new FormRow();
                r1.put("itemNumber", rs.getString(1));
                r1.put("itemDescription", rs.getString(2));
                r1.put("primaryUOM",rs.getString(3));
                inventoryId = rs.getString(4);
                r1.put("INVENTORY_ITEM_ID", inventoryId);
                String val = null;
                try{
                    val =  jsonCall(inventoryId,subInventory);
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
    public static String jsonCall(String inventoryId,String subInventory) throws IOException {
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
        while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        System.out.println("Response status: " + status);
        System.out.println(content.toString());
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
}
APICall.getFormData(element, primaryKey, formData);

