package org.joget.geowatch.app;

import org.joget.apps.app.service.AppPluginUtil;
import org.joget.geowatch.api.configuration.MapApi;

public class AppProperties {
    private static final String CLAS = MapApi.class.getName();

    public static final String PLUGIN_GROUP_ID;
    public static final String PLUGIN_ARTIFACT_ID;
    public static final String PLUGIN_VERSION;

    public static final String PLUGIN_SCHEMA;
    public static final String PLUGIN_MODE;
    public static final Boolean PLUGIN_DEBUG_MODE;
    public static final Boolean PLUGIN_PROD_MODE;
    public static final int PLUGIN_MINUTE_BEFORE_LIVE;

    static {
        PLUGIN_GROUP_ID = AppPluginUtil.getMessage("plugin.group.id", CLAS, "/project");
        PLUGIN_ARTIFACT_ID = AppPluginUtil.getMessage("plugin.artifact.id", CLAS, "/project");
        PLUGIN_VERSION = AppPluginUtil.getMessage("plugin.version", CLAS, "/project");

        PLUGIN_SCHEMA = PLUGIN_VERSION.split("-")[1];
        PLUGIN_MODE = PLUGIN_VERSION.split("-")[2];
        PLUGIN_DEBUG_MODE = PLUGIN_MODE.equals("debug");
        PLUGIN_PROD_MODE = PLUGIN_MODE.equals("prod");

        PLUGIN_MINUTE_BEFORE_LIVE = Integer.decode(AppPluginUtil.getMessage("plugin.minute.before.live", CLAS, "/project"));
    }

    public static final String PLUGIN_FULL_NAME = PLUGIN_ARTIFACT_ID + "-" + PLUGIN_VERSION;

    //GHT
    public static final String GHT_SCHEME = PLUGIN_SCHEMA;
    public static final String GHT_BASE_URL;
    public static final String GHT_VEHICLE_API_KEY;
    public static final String GHT_TOUR_API_KEY;
    public static final String GHT_CUSTOMER_NAME;

    static {// GHT Properties
        GHT_BASE_URL = AppPluginUtil.getMessage(String.format("ght.%s.base.url", GHT_SCHEME), CLAS, "/api_config");
        GHT_CUSTOMER_NAME = AppPluginUtil.getMessage(String.format("ght.%s.customer.name", GHT_SCHEME), CLAS, "/api_config");
        GHT_VEHICLE_API_KEY = AppPluginUtil.getMessage(String.format("ght.%s.vehicle.api.key", GHT_SCHEME), CLAS, "/api_config");
        GHT_TOUR_API_KEY = AppPluginUtil.getMessage(String.format("ght.%s.tour.api.key", GHT_SCHEME), CLAS, "/api_config");
    }

    // Google
    public static final String GOOGLE_API_KEY;
    public static final String GOOGLE_API_SNAPSHOT_RESOLUTIONS;

    static {// Google Properties
        GOOGLE_API_KEY = AppPluginUtil.getMessage("google.api.key", CLAS, "/api_config");
        GOOGLE_API_SNAPSHOT_RESOLUTIONS = AppPluginUtil.getMessage("google.api.snapshot.resolutions", CLAS, "/api_config");
    }

    public static final String JOGET_SYSTEM_LOCALE;
    public static final String PLUGIN_JOGET_VERSION;
    public static final String PLUGIN_JOGET_DESCRIPTION;
    public static final String JOGET_PROCESS_NOTIFY_NAME;
    public static final String JOGET_PROCESS_TRIP_LIVE_ROUTE_ID;
    public static final String JOGET_PROCESS_TRIP_POD_ROUTE_ID;
    public static final String JOGET_PLUGIN_USER;

    static {
        JOGET_SYSTEM_LOCALE  = AppPluginUtil.getMessage("joget.system.locale", CLAS, "/project");
        PLUGIN_JOGET_VERSION = AppPluginUtil.getMessage("joget.plugin.version", CLAS, "/project");
        PLUGIN_JOGET_DESCRIPTION = AppPluginUtil.getMessage("joget.plugin.description", CLAS, "/project");
        JOGET_PROCESS_NOTIFY_NAME = AppPluginUtil.getMessage("joget.process.notify.name", CLAS, "/project");
        JOGET_PROCESS_TRIP_LIVE_ROUTE_ID = AppPluginUtil.getMessage("joget.process.trip.live.route.id", CLAS, "/project");
        JOGET_PROCESS_TRIP_POD_ROUTE_ID = AppPluginUtil.getMessage("joget.process.trip.pod.route.id", CLAS, "/project");
        JOGET_PLUGIN_USER = AppPluginUtil.getMessage("joget.plugin.user", CLAS, "/project");
    }

    public static final double ANALYZE_ROUTE_TOLERANCE;
    public static final double ANALYZE_GEOFENCE_TOLERANCE;

    static {
        ANALYZE_ROUTE_TOLERANCE = Double.parseDouble(AppPluginUtil.getMessage("analyze.route.tolerance", CLAS, "/project"));
        ANALYZE_GEOFENCE_TOLERANCE = Integer.parseInt(AppPluginUtil.getMessage("analyze.geofence.tolerance", CLAS, "/project"));
    }

    public static final String ROUTE_READ_PERMISSION;
    public static final String ROUTE_WRITE_PERMISSION;
    public static final String VEHICLE_READ_PERMISSION;
    public static final String VEHICLE_WRITE_PERMISSION;
    public static final String LIVE_TRIP_READ_PERMISSION;
    public static final String LIVE_TRIP_WRITE_PERMISSION;
    public static final String NOTIFY_READ_PERMISSION;
    public static final String NOTIFY_WRITE_PERMISSION;

    static {
        ROUTE_READ_PERMISSION = AppPluginUtil.getMessage("route.read", CLAS, "/user_permission");
        ROUTE_WRITE_PERMISSION = AppPluginUtil.getMessage("route.write", CLAS, "/user_permission");
        VEHICLE_READ_PERMISSION = AppPluginUtil.getMessage("vehicle.read", CLAS, "/user_permission");
        VEHICLE_WRITE_PERMISSION = AppPluginUtil.getMessage("vehicle.write", CLAS, "/user_permission");
        LIVE_TRIP_READ_PERMISSION = AppPluginUtil.getMessage("life.trip.read", CLAS, "/user_permission");
        LIVE_TRIP_WRITE_PERMISSION = AppPluginUtil.getMessage("life.trip.write", CLAS, "/user_permission");
        NOTIFY_READ_PERMISSION = AppPluginUtil.getMessage("notify.read", CLAS, "/user_permission");
        NOTIFY_WRITE_PERMISSION = AppPluginUtil.getMessage("notify.write", CLAS, "/user_permission");
    }

    public static final String KEY_EMAIL_BODY_NOTIFICATION_LEFT_THE_ROUTE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_RETURNED_TO_ROUTE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_ZONE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_ZONE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_OPEN_DOOR;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_NEED_SUBMIT_POD;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_START_ZONE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_START_ZONE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_FINISH_ZONE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_FINISH_ZONE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_GHTRACK;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_DELAY_START_ZONE;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_DELAY_FINISH_ZONE;
    public static final String KEY_ROUTE_EXIST;
    public static final String KEY_ROUTE_NOT_EXIST;
    public static final String KEY_GEOFENCE_EXIST;
    public static final String KEY_GEOFENCE_NOT_EXIST;
    
    public static final String KEY_EMAIL_BODY_NOTIFICATION_NO_DATA;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_STOPPED_UNKNOWN_LOCATION;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_STOPPED_BLACKLIST_LOCATION;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_STOPPED_REDZONE_LOCATION;
    public static final String KEY_EMAIL_BODY_NOTIFICATION_DELAY_START_NEW;
    
    
    
    

    static {
        KEY_EMAIL_BODY_NOTIFICATION_LEFT_THE_ROUTE = AppPluginUtil.getMessage("key.email.body.notification.left.the.route", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_RETURNED_TO_ROUTE = AppPluginUtil.getMessage("key.email.body.notification.returned.to.route", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_ZONE = AppPluginUtil.getMessage("key.email.body.notification.entered.to.zone", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_ZONE = AppPluginUtil.getMessage("key.email.body.notification.leave.from.zone", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_OPEN_DOOR = AppPluginUtil.getMessage("key.email.body.notification.open.door", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_NEED_SUBMIT_POD = AppPluginUtil.getMessage("key.email.body.notification.need.submit.pod", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_START_ZONE = AppPluginUtil.getMessage("key.email.body.notification.entered.to.start.zone", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_START_ZONE = AppPluginUtil.getMessage("key.email.body.notification.leave.from.start.zone", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_FINISH_ZONE = AppPluginUtil.getMessage("key.email.body.notification.entered.to.finish.zone", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_FINISH_ZONE = AppPluginUtil.getMessage("key.email.body.notification.leave.from.finish.zone", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_GHTRACK = AppPluginUtil.getMessage("key.email.body.notification.ght", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_DELAY_START_ZONE = AppPluginUtil.getMessage("key.email.body.notification.delay.start.zone", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_DELAY_FINISH_ZONE = AppPluginUtil.getMessage("key.email.body.notification.delay.finish.zone", CLAS, "/project");
        
        //new code
        
        KEY_EMAIL_BODY_NOTIFICATION_NO_DATA = AppPluginUtil.getMessage("key.email.body.notification.no.data", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_STOPPED_UNKNOWN_LOCATION = AppPluginUtil.getMessage("key.email.body.notification.stopped.unknown.location", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_STOPPED_BLACKLIST_LOCATION = AppPluginUtil.getMessage("key.email.body.notification.stopped.blacklist.location", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_STOPPED_REDZONE_LOCATION = AppPluginUtil.getMessage("key.email.body.notification.stopped.redzone.location", CLAS, "/project");
        KEY_EMAIL_BODY_NOTIFICATION_DELAY_START_NEW = AppPluginUtil.getMessage("key.email.body.notification.delay.start", CLAS, "/project");
        
        
        
        
        KEY_ROUTE_EXIST = AppPluginUtil.getMessage("key.route.exist", CLAS, "/project");
        KEY_ROUTE_NOT_EXIST = AppPluginUtil.getMessage("key.route.not.exist", CLAS, "/project");
        KEY_GEOFENCE_EXIST = AppPluginUtil.getMessage("key.geofence.exist", CLAS, "/project");
        KEY_GEOFENCE_NOT_EXIST = AppPluginUtil.getMessage("key.geofence.not.exist", CLAS, "/project");
    }
}
