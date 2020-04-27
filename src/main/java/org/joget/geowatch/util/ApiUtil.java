package org.joget.geowatch.util;

import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.Location;
import org.joget.geowatch.api.dto.LogJson;
import org.joget.geowatch.api.dto.VehicleJson;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.type.VehicleType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.joget.geowatch.util.DateUtil.getTimeZone;

/**
 * Created by k.lebedyantsev
 * Date: 1/5/2018
 * Time: 12:48 PM
 */
public class ApiUtil {
    private static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    private static final String DB_ROUTE_DATE_FORMAT = "yyyy-MM-dd XXX HH:mm";

    public static VehicleJson remapGhtVehicleDtoToVehicleJson(String vehicleId, GhtVehicleInnerEntity ghtVehicle, VehicleType type) {
        VehicleJson bean = null;
        if (ghtVehicle != null) {
            bean = new VehicleJson();
            bean.setVehicleId(vehicleId);
            bean.setType(type);
            bean.setAllLocations(new ArrayList<Location>());
            bean.setCurrentPosition(new Location());
            bean.setLicensePlate(ghtVehicle.getId());
        }
        return bean;
    }

    public static String getDbDateFormat() {
        return DB_DATE_FORMAT;
    }

    // ToDo: Need Remove this
    public static String getDbRouteDateFormat() {
        return DB_ROUTE_DATE_FORMAT;
    }


    public static List<LogJson> remapListLogDtoToListLogJson(List<Log> tripLogs, User currentUser) {
        List<LogJson> list = new ArrayList<>();
        if (tripLogs != null && tripLogs.size() > 0) {
            for (Log log : tripLogs) {
                LogJson bean = remapLogDtoToLogJson(log, currentUser);
                list.add(bean);
            }
        }
        return list;
    }

    public static LogJson remapLogDtoToLogJson(Log log, User currentUser) {
        LogJson bean = new LogJson();
        if (log != null) {
            bean.setId(log.getId());
            bean.setVehicleId(log.getVehicleId());
            bean.setTripId(log.getTripId());

            SimpleDateFormat format = getFormatter(getDbDateFormat());
            if (currentUser != null)
                format.setTimeZone(getTimeZone(currentUser));

            log.setDate(log.getServerDate());
            bean.setDate(format.format(log.getServerDate()));
            bean.setDoor1State(log.getDoor1State());
            bean.setDoor2State(log.getDoor2State());
            bean.setDoor3State(log.getDoor3State());
            bean.setDoor4State(log.getDoor4State());

            if (isNotBlank(log.getTempSensor1())) {
                bean.setTempSensor1(Double.valueOf(log.getTempSensor1()));
            }
            if (isNotBlank(log.getTempSensor2())) {
                bean.setTempSensor2(Double.valueOf(log.getTempSensor2()));
            }
            if (isNotBlank(log.getTempSensor3())) {
                bean.setTempSensor3(Double.valueOf(log.getTempSensor3()));
            }
            if (isNotBlank(log.getTempSensor4())) {
                bean.setTempSensor4(Double.valueOf(log.getTempSensor4()));
            }
            if (isNotBlank(log.getTempSensor5())) {
                bean.setTempSensor5(Double.valueOf(log.getTempSensor5()));
            }
            if (isNotBlank(log.getTempSensor6())) {
                bean.setTempSensor6(Double.valueOf(log.getTempSensor6()));
            }
            if (isNotBlank(log.getTempSensor7())) {
                bean.setTempSensor7(Double.valueOf(log.getTempSensor7()));
            }
            if (isNotBlank(log.getTempSensor8())) {
                bean.setTempSensor8(Double.valueOf(log.getTempSensor8()));
            }
            if (isNotBlank(log.getTempSensor9())) {
                bean.setTempSensor9(Double.valueOf(log.getTempSensor9()));
            }
            if (isNotBlank(log.getTempSetpoint1())) {
                bean.setTempSetpoint1(Double.valueOf(log.getTempSetpoint1()));
            }
            if (isNotBlank(log.getTempSetpoint2())) {
                bean.setTempSetpoint2(Double.valueOf(log.getTempSetpoint2()));
            }
            if (isNotBlank(log.getTempSetpoint3())) {
                bean.setTempSetpoint3(Double.valueOf(log.getTempSetpoint3()));
            }
            if (isNotBlank(log.getSpeed())) {
                bean.setSpeed(Double.valueOf(log.getSpeed()));
            }
            if (isNotBlank(log.getLat()) && isNotBlank(log.getLng())) {
                Location location = new Location();
                location.setLat(Double.valueOf(log.getLat()));
                location.setLng(Double.valueOf(log.getLng()));
                location.setLogId(log.getId());
                bean.setLocation(location);
            }
        }
        return bean;
    }

    public static SimpleDateFormat getFormatter(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        SimpleTimeZone stz = new SimpleTimeZone((int) TimeUnit.HOURS.toMillis(0), "UTC");
        format.setTimeZone(stz);
        return format;
    }
}
