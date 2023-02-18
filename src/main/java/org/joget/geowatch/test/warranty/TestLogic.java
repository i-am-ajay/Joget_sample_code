package org.joget.geowatch.test.warranty;

public class TestLogic {
    public static void main(String [] args){
        String parameter = "1,2,3,4";
        StringBuilder builder = new StringBuilder();
        String [] paramArray = parameter.split(",");
        for(String str : paramArray){
            builder.append(str).append("\",\"");
        }
        builder.deleteCharAt(builder.length()-1);
        builder.deleteCharAt(builder.length()-1);
        System.out.println(builder.toString());
    }
}
