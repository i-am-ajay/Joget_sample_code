package org.joget.geowatch.ghtrack.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joget.commons.util.LogUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.joget.geowatch.app.AppProperties.*;

/**
 * Created by klebedyantsev on 2017-12-28
 */
public abstract class AbstractGhTrackService {
    static final String VEHICLE_CALL = "vehicle";
    static final String TOUR_CALL = "tour";


    Object doExternalCall(String urlString, String typeCall, String method, Object requestBean, Class responseClass, Class errorMessageClass) {
        Gson gson = new GsonBuilder().create();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(GHT_BASE_URL + urlString);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            switch (typeCall) {
                case VEHICLE_CALL:
                    conn.setRequestProperty("X-Api-Key", GHT_VEHICLE_API_KEY);
                    break;
                case TOUR_CALL:
                    conn.setRequestProperty("X-Api-Key", GHT_TOUR_API_KEY);
                    break;
            }

            conn.setRequestProperty("Content-Type", "application/json");
            if (requestBean != null) {
                conn.setDoInput(true);
                DataOutputStream wr = new DataOutputStream(
                        conn.getOutputStream());
                wr.writeBytes(gson.toJson(requestBean));
                wr.flush();
                wr.close();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_UNAUTHORIZED) {
                BufferedReader response;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    response = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                } else {
                    response = new BufferedReader(new InputStreamReader(
                            conn.getErrorStream()));
                }
                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK:
                        return gson.fromJson(response, responseClass);
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        return gson.fromJson(response, errorMessageClass);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            LogUtil.error(AbstractGhTrackService.class.getName(), e, "doExternalCall Error!");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

}
