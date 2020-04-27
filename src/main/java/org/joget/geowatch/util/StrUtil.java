package org.joget.geowatch.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.IOUtils;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.geowatch.api.configuration.MapApi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class StrUtil {

    private static final Gson GSON = new GsonBuilder().create();

    public static String getStr(Object obj) {
        if (obj == null) return "";
        if (obj instanceof String) return (String) obj;
        return GSON.toJson(obj);
    }

    public static void getStr(Object obj, Appendable writer) {
        GSON.toJson(obj, writer);
    }

    public static <T> T getObj(Reader reader, Class<T> type) throws Exception {
        return GSON.fromJson(reader, type);
    }

    public static <T> T getObj(String str, Class<T> type) throws Exception {
        if (String.class.equals(type)) return (T) str;
        return GSON.fromJson(str, type);
    }

    public static String getStr(Reader reader) throws Exception {
        if (reader == null) return "";
        return IOUtils.toString(reader);
    }

//    public static String getStr(HttpServletRequest request) throws Exception {
//        return getStr(request.getInputStream());
//    }

    public static String getStr(InputStream inputStream) throws Exception {
        return IOUtils.toString(inputStream, "UTF-8");
    }

    public static <T> List<T> getList(String str, TypeToken<List<T>> type) throws Exception {
        List<T> res = null;
        if (isNotBlank(str))
            res = GSON.fromJson(str, type.getType());
        return res != null ? res : new ArrayList<T>();
    }

    public static <K, V> Map<K, V> getMap(String str, Class<K> keyType, Class<V> valueType) throws Exception {
        if (isBlank(str)) return null;

        Map<K, V> res = null;
        if (isNotBlank(str))
            res = GSON.fromJson(str, new TypeToken<Map<K, V>>(){}.getType());
        return res;
    }

    public static <T> T getObj(String str, TypeToken<T> typeToken) throws Exception {
        if(isEmpty(str)) return null;
        if (typeToken == null) return null;

        T res = null;
        if (isNotBlank(str))
            res = GSON.fromJson(str, typeToken.getType());
        return res;
    }

    public static <T> T getObj(InputStream is, Class<T> type) throws Exception {
        if(is == null) return null;
        JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        T obj = GSON.fromJson(reader, type);
        return obj;
    }

    private static final String CLAS = MapApi.class.getName();

    public static String i18n(String key) {
        return AppPluginUtil.getMessage(key, CLAS, "message/GglGeoWatchPlugin");
    }

}
