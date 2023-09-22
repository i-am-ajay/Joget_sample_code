package org.joget.geowatch.learning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class AjaxFormBinder {
    public static FormRowSet load(String[] values) {
        LogUtil.info("Calling","Load Method");
        FormRowSet rows = new FormRowSet();

        //set groupId based on dependency value
        String pinCode = null;
        if (values != null && values.length > 0) {
            pinCode = values[0];
        }
        LogUtil.info("Pin Code",pinCode);
        try{
            rows = apiCall(pinCode);
        }
        catch(IOException ex){
            LogUtil.error(AjaxFormBinder.class.getName(),ex,ex.getMessage());
        }
        return rows;
    }

    public static FormRowSet apiCall(String id) throws IOException {
        LogUtil.info("Calling","Api Method");
        String urlString = "https://api.postalpincode.in/pincode/" + id;
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        //con.setRequestProperty("Authorization", "Basic aXQucGF2YW46d2VsY29tZTEy");
        con.setRequestProperty("Content-Type", "application/json");

        /* Payload support */
        con.setDoOutput(true);
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        LogUtil.info("Response status: " , Integer.toString(status));
        return parseJsonObject(content.toString());
    }

    public static FormRowSet parseJsonObject(String jsonString) {
        LogUtil.info("Calling","Parse Json Object Method");

        FormRowSet set = new FormRowSet();
        try {
            JSONArray mainArray = new JSONArray(jsonString);
            JSONObject object = mainArray.getJSONObject(0);
            //JSONObject object = new JSONObject(jsonString);
            JSONArray jsonArray = object.getJSONArray("PostOffice");
            for (int i = 0; i < jsonArray.length(); i++) {
                object = jsonArray.getJSONObject(i);
                LogUtil.info("Name",(object.get("Name").toString()));
                FormRow option = new FormRow();
                option.setProperty(FormUtil.PROPERTY_VALUE, object.get("Name").toString());
                option.setProperty(FormUtil.PROPERTY_LABEL, object.get("Name").toString());
                set.add(option);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return set;
//call load method with injected variable
    }
}
//AjaxFormBinder.load(values);
