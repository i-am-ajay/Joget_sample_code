package org.joget.geowatch.api.dto;

import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.db.dto.inner.VehicleLastPositionInnerEntity;

/**
 * Created by k.lebedyantsev
 * Date: 2/27/2018
 * Time: 2:03 PM
 */
public class Location {
    private Double lat;
    private Double lng;
    private String logId;
    private String notifyId;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    static public Location update1(Location item, Notify notify) throws Exception{
        if(item == null) return item;
        if(notify == null) return item;
        if(notify.getEvent1().getLat() == null || notify.getEvent1().getLng() == null) return item;

        item.lat = Double.valueOf(notify.getEvent1().getLat());
        item.lng = Double.valueOf(notify.getEvent1().getLng());
        item.logId = notify.getEvent1().getLogId();
        item.notifyId = notify.getEvent1Id();
        return item;
    }

    static public Location update(Location item, Log log){
        if(item == null) return null;
        if(log == null) return item;
        if(log.getLat() == null || log.getLng() == null) return item;

        item.lat = Double.valueOf(log.getLat());
        item.lng = Double.valueOf(log.getLng());
        item.logId = log.getId();

        return item;
    }

    static public Location update(Location item, VehicleLastPositionInnerEntity vehicleLastPositionEntity){
        if(item == null) return null;
        if(vehicleLastPositionEntity == null) return item;

        item.lat = Double.valueOf(vehicleLastPositionEntity.getLat());
        item.lng = Double.valueOf(vehicleLastPositionEntity.getLng());
        item.logId = vehicleLastPositionEntity.getLogId();

        return item;
    }
}
