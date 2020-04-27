package org.joget.geowatch.db.dto.inner;

import org.joget.geowatch.db.dto.Log;

import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 6/26/2018
 * Time: 12:47 PM
 */
public class VehicleLastPositionInnerEntity {
    private String logId;
    private String lat;
    private String lng;
    private Date date;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static VehicleLastPositionInnerEntity update(VehicleLastPositionInnerEntity item, Log log) {
        if(item == null) return null;
        if(log == null) return item;

        item.lat = log.getLat();
        item.lng = log.getLng();
        item.logId = log.getId();
        item.date = log.getDate();

        return item;
    }

    public static VehicleLastPositionInnerEntity create(Log log) {
        if (log == null) return null;

        VehicleLastPositionInnerEntity item = new VehicleLastPositionInnerEntity();
        item.lat = log.getLat();
        item.lng = log.getLng();
        item.logId = log.getId();
        item.date = log.getDate();

        return item;
    }
}
