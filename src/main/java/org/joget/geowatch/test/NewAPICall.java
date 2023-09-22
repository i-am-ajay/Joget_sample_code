package org.joget.geowatch.test;

import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewAPICall {
    public static String format() throws JSONException {
        JSONObject object = data;
        JSONArray array = object.getJSONArray("value");
        JSONObject subObject = array.getJSONObject(0);
        JSONObject newObject = new JSONObject();
        newObject.put("subject",subObject.get("subject"));
        newObject.put("id",subObject.get("id"));
        JSONObject baseObject = new JSONObject();
        baseObject.append("values",newObject);
        System.out.println(baseObject.toString());
        LogUtil.info("JSON Object",subObject.get("body").toString());
        return baseObject.toString();
    }
}
return NewAPICall.format();
