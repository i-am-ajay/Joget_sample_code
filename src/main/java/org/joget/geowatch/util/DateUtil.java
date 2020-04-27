package org.joget.geowatch.util;

import org.joget.commons.util.TimeZoneUtil;
import org.joget.directory.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joget.geowatch.app.AppProperties.JOGET_SYSTEM_LOCALE;
import static org.joget.geowatch.util.JogetUtil.getEnvVar;

public class DateUtil {

    public static TimeZone getTimeZone(User user) {
        if (user != null) {
            return TimeZone.getTimeZone(TimeZoneUtil.getTimeZoneByGMT(user.getTimeZone()));
        } else {
            return TimeZone.getTimeZone(TimeZoneUtil.getServerTimeZoneID());
        }
    }

    /**
     * UI_DATE_SHORT
     */
    private static final String DEF_JAVA_UI_DATE_TIME_SHORT = "dd-MM-yyyy HH:mm";

    public static String getUiShortStrDate(Date date, User user) throws Exception {
        return getUiShortStrDate(date, user, JOGET_SYSTEM_LOCALE);
    }

    public static String getUiShortStrDate(Date date, User user, String defLocalStr) throws Exception {
        if (date == null) return null;

        String userLoacalStr = user != null ? user.getLocale() : defLocalStr;

        String tpStr = getEnvVar("javaUiDateTimeShort", userLoacalStr, DEF_JAVA_UI_DATE_TIME_SHORT, defLocalStr);
        SimpleDateFormat tp = new SimpleDateFormat(tpStr);

        tp.setTimeZone(getTimeZone(user));
        return tp.format(date);
    }

    /**
     * JOGET_DATE_FORMAT
     */
    private static final SimpleDateFormat JOGET_DATE_FORMAT;

    static {
        JOGET_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JOGET_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static Date getJogetDate(String strDateTime) throws Exception {
        if (isEmpty(strDateTime)) return null;
        return JOGET_DATE_FORMAT.parse(strDateTime);
    }

    /**
     * GHT_DATE_FORMATTER
     */
    private static final SimpleDateFormat GHT_DATE_FORMATTER;

    static {
        GHT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        GHT_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static Date getGhtDate(String strDate) throws Exception {
        if (isBlank(strDate)) return null;
        return GHT_DATE_FORMATTER.parse(strDate);
    }

    public static String getGhtStrDate(Date date) throws Exception {
        if (date == null) return null;
        return GHT_DATE_FORMATTER.format(date);
    }

//    /**
//     * UI_DATE_FORMATTER
//     */
//    private static SimpleDateFormat UI_DATE_FORMATTER;
//
//    static {
//        UI_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    }
//
//    public static void setupUiDate(String dateTemplate) throws Exception {
//        UI_DATE_FORMATTER = new SimpleDateFormat(dateTemplate);
//    }
//
//    public static String getUiStrDate(Date date, User user) throws Exception {
//        if (date == null) return null;
//        if (user == null) return UI_DATE_FORMATTER.format(date);
//        SimpleDateFormat tp = (SimpleDateFormat) UI_DATE_FORMATTER.clone();
//        tp.setTimeZone(getTimeZone(user));
//        return tp.format(date);
//    }

    /**
     * FE_API_DATE_FORMATTER
     */
    private static final String FE_API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS XXX";
    public static final SimpleDateFormat FE_API_DATE_FORMATTER;

    static {
        FE_API_DATE_FORMATTER = new SimpleDateFormat(FE_API_DATE_FORMAT);
    }

    public static Date getFeApiDate(String strDate) throws Exception {
        if (isEmpty(strDate)) return null;
        return FE_API_DATE_FORMATTER.parse(strDate);
    }

    public static String getFeApiStrDate(Date date) throws Exception {
        if (date == null) return null;
        return FE_API_DATE_FORMATTER.format(date);
    }

    /**
     * DB_DATE_FORMATTER
     */
    private static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    public static final SimpleDateFormat DB_DATE_FORMATTER;

    static {
        DB_DATE_FORMATTER = new SimpleDateFormat(DB_DATE_FORMAT);
    }

    public static Date getDbDate(String strDate) throws Exception {
        if (isBlank(strDate)) return null;
        return DB_DATE_FORMATTER.parse(strDate);
    }

    public static String getDbDate(Date date) throws Exception {
        if (date == null) return null;
        return DB_DATE_FORMATTER.format(date);
    }
}
