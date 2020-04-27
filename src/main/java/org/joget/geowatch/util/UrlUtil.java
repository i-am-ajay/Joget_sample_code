package org.joget.geowatch.util;

import org.joget.commons.util.LogUtil;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.lang3.StringUtils.replaceAll;
import static org.joget.geowatch.app.AppProperties.PLUGIN_DEBUG_MODE;
import static org.joget.geowatch.util.StrUtil.getStr;

public class UrlUtil {
    private static final String TAG = UrlUtil.class.getSimpleName();

    public static <R, E> RespResult<R, E> getObj(
            Class<R> resultClas, Class<E> errorClass,
            Method method, String strUrl, String[] params, Object body,
            Header[] headers) throws Exception {

        String strResultObj = null;
        String strErrorObj = null;

        String strBody = null;

        RespResult<R, E> res = new RespResult<>();
        HttpURLConnection con = null;
        try {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i] == null) params[i] = "";
                    else params[i] = getUtf8(params[i]);
                }
                strUrl = String.format(strUrl, params);
            }

            URL url = new URL(strUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method.name());
            con.setDoOutput(true);
            if (headers != null) {
                for (Header h : headers)
                    con.addRequestProperty(h.key, h.value);
            }

            if (body != null) {
                con.setDoInput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                strBody = getStr(body);
                wr.writeBytes(strBody);
                wr.flush();
                wr.close();
            }

            res.status = con.getResponseCode();
            if (HTTP_OK == res.status) {
                strResultObj = getStr(con.getInputStream());
                res.resultObj = StrUtil.getObj(strResultObj, resultClas);
            } else if (errorClass != null) {
                strErrorObj = getStr(con.getErrorStream());
                res.errorObj = StrUtil.getObj(strErrorObj, errorClass);
            }
            return res;
        } finally {
            if (con != null) con.disconnect();

            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "OUTGOING URL: " + strUrl);
                LogUtil.info(TAG, "OUTGOING STATUS: " + res.status);
                LogUtil.info(TAG, "OUTGOING BODY: " + replaceAll(strBody, "\\s", ""));
                LogUtil.info(TAG, "OUTGOING ERROR: " + replaceAll(strErrorObj, "\\s", ""));
                LogUtil.info(TAG, "OUTGOING RESULT: " + replaceAll(strResultObj, "\\s", ""));
            }
        }
    }

    public static String getUtf8(String str) throws Exception {
        if (str == null) return "";
        return URLEncoder.encode(str, "UTF-8");
    }

    public enum Method {
        GET,
        POST
    }

    public static class Param {
        public String param;

        public Param(String param) {
            this.param = param;
        }
    }

    public static class Header {
        public String key;
        public String value;

        public Header(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static class RespResult<R, E> {
        protected int status;
        protected R resultObj;
        protected E errorObj;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public R getResultObj() {
            return resultObj;
        }

        public void setResultObj(R resultObj) {
            this.resultObj = resultObj;
        }

        public E getErrorObj() {
            return errorObj;
        }

        public void setErrorObj(E errorObj) {
            this.errorObj = errorObj;
        }
    }
}
