package org.joget.geowatch.util;

import com.google.maps.model.LatLng;
import org.joget.commons.util.LogUtil;
import org.joget.geowatch.util.geo.PolyUtil;
import org.springframework.util.Base64Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.joget.geowatch.app.AppProperties.GOOGLE_API_KEY;
import static org.joget.geowatch.app.AppProperties.GOOGLE_API_SNAPSHOT_RESOLUTIONS;
import static org.joget.geowatch.app.AppProperties.PLUGIN_DEBUG_MODE;

/**
 * Created by k.lebedyantsev
 * Date: 7/19/2018
 * Time: 2:00 PM
 */
public class SnapshotUtil {
    private static final String TAG = SnapshotUtil.class.getSimpleName();
    private static final String snapshotUrl = String.format(
            "https://maps.googleapis.com/maps/api/staticmap?size=%s&key=%s&path=enc:",
            GOOGLE_API_SNAPSHOT_RESOLUTIONS, GOOGLE_API_KEY);

    public static String snip(List<LatLng> latLngList) {
        String polylineHash = PolyUtil.encode(latLngList);
        String imgBase64 = null;
        StringBuilder stringBuilder = new StringBuilder(snapshotUrl);
        stringBuilder.append(polylineHash);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(stringBuilder.toString());

            conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            BufferedInputStream response;
            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "SnapshotUtil request for snip url: " + stringBuilder.toString() + "; responseCode: " + responseCode);
            }
            if (responseCode == HTTP_OK) {
                response = new BufferedInputStream(
                        conn.getInputStream());

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n;
                while (-1 != (n = response.read(buf))) {
                    out.write(buf, 0, n);
                }
                response.close();
                out.close();
                buf = out.toByteArray();
                imgBase64 = Base64Utils.encodeToString(buf);
            } else {
                throw new Exception("Some error from google API: " + conn.getResponseMessage());
            }
            return imgBase64;
        }catch (Exception e){
            LogUtil.error(TAG, e, "Error on creating snapshot.");
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
