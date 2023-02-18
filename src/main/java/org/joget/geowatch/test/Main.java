// For JsonArray pass your grid and for FormRow pass row.
public static FormRow processJsonArray(JSONArray array, FormRow row){
        for(int i=0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            row.setProperty("oc_no",processColumn(obj.getString("OC_NO")));
            row.setProperty("PRODUCT_LINE",processColumn(obj.getString("PRODUCT_LINE")));
            row.setProperty("category",processColumn(obj.getString("CATEGORY")));
            row.setProperty("country_of_supply",processColumn(obj.getString("COUNTRY_OF_SUPPLY")));
            row.setProperty("order_month",processColumn(obj.getString("ORDER_MONTH")));
            rowSet.add(row);
        }
        return row;
        }
public static String processColumn(Object name) {
    return (name == null ? "" : name.toString());
}