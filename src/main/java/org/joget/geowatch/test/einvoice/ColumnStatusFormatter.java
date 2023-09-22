package org.joget.geowatch.test.einvoice;

import org.joget.apps.form.model.FormRow;


public class ColumnStatusFormatter {
    public static String format(){
        String totalCount = "0";
        String statusCode = (String)row.get("invh_status_code");
        if(statusCode == null || statusCode.isEmpty()){
            return "Pending";
        }
        else if(statusCode.equals("200")){
            return "Validated";
        }
        else{
            return "Validation Failed";
        }
    }
}
ColumnStatusFormatter.format(row);
