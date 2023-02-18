package org.joget.geowatch.test;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormattingTest {
    public static void main(String [] args){
        String date = "Sat Jan 23 00:12:00 GST 2021";
        LocalDate time = LocalDate.parse(date,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(time.toString());
    }
}
