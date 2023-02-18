package org.joget.geowatch.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestReserveStockAPI {
    public static void main(String[] args) throws IOException {
        getReserveStock();
    }

    public static String getReserveStock() throws IOException {
        URL url = new URL("http://192.168.5.216:8080/ords/ommrest/ommjob/v1/Job_No/10029");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic aXQuYW1pdG06b3JhY2xlMTIzNA==");
        con.setRequestProperty("Content-Type", "application/json");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        System.out.println("Response status: " + status);
        return content.toString();
    }
}
