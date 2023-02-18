package org.joget.geowatch.test;

import org.json.JSONObject;

public class GetReservedStock {
    static String authorization = "#envVariable.ommrestapiauthentication#";
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
