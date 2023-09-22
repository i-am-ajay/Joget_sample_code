package org.joget.geowatch.learning;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormUtil;


public class DemoFormOptionBinder {
    public static FormRowSet load(Element element, String primaryKey, FormData formData) {
        FormRowSet rows = new FormRowSet();
        //Get timezones using timezone util
        Map yearMonthMap = generateYearMonth();
        for(Object entry : yearMonthMap.entrySet()){
            Map.Entry entryPair = (Map.Entry)entry;
            FormRow row = new FormRow();
            row.setProperty(FormUtil.PROPERTY_VALUE,entryPair.getValue().toString() );
            row.setProperty(FormUtil.PROPERTY_LABEL, entryPair.getKey().toString());
            rows.add(row);
        }
        return rows;
    }

    public static Map generateYearMonth(){
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        Month  [] monthArray = Month.values();
        Map yearMonthMap = new LinkedHashMap();
        for(Month m : monthArray){
            yearMonthMap.put(m.name()+"-"+year, YearMonth.of(year,m));
        }
        return yearMonthMap;
    }
}
DemoFormOptionBinder.load(element, primaryKey, formData);
