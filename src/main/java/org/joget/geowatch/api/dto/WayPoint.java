package org.joget.geowatch.api.dto;

import org.joget.geowatch.type.ZoneType;
import org.joget.geowatch.type.WayPointType;

import java.util.Collection;

/**
 * Created by k.lebedyantsev
 * Date: 1/3/2018
 * Time: 1:30 PM
 */
public class WayPoint {
    private String id;
    private String name;
    private Integer number;
    private String description;
    private WayPointType wayPointType;
    private ZoneType zoneType;
    private String geofenceNotifyType;
    private String endpointNotifyType;
    private String placeId;
    private Double lat;
    private Double lng;
    private Long radius;
    private String polygonHash;
    private Integer duration;
    private String timeWindowBegin;
    private String timeWindowEnd;
    private String actualTimeWindowBegin;
    private String actualTimeWindowEnd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WayPointType getWayPointType() {
        return wayPointType;
    }

    public void setWayPointType(WayPointType wayPointType) {
        this.wayPointType = wayPointType;
    }

    public ZoneType getZoneType() {
        return zoneType;
    }

    public void setZoneType(ZoneType zoneType) {
        this.zoneType = zoneType;
    }

    public String getGeofenceNotifyType() {
        return geofenceNotifyType;
    }

    public void setGeofenceNotifyType(String geofenceNotifyType) {
        this.geofenceNotifyType = geofenceNotifyType;
    }

    public String getEndpointNotifyType() {
        return endpointNotifyType;
    }

    public void setEndpointNotifyType(String endpointNotifyType) {
        this.endpointNotifyType = endpointNotifyType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

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

    public Long getRadius() {
        return radius;
    }

    public void setRadius(Long radius) {
        this.radius = radius;
    }

    public String getPolygonHash() {
        return polygonHash;
    }

    public void setPolygonHash(String polygonHash) {
        this.polygonHash = polygonHash;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getTimeWindowBegin() {
        return timeWindowBegin;
    }

    public void setTimeWindowBegin(String timeWindowBegin) {
        this.timeWindowBegin = timeWindowBegin;
    }

    public String getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(String timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }

    public String getActualTimeWindowBegin() {
        return actualTimeWindowBegin;
    }

    public void setActualTimeWindowBegin(String actualTimeWindowBegin) {
        this.actualTimeWindowBegin = actualTimeWindowBegin;
    }

    public String getActualTimeWindowEnd() {
        return actualTimeWindowEnd;
    }

    public void setActualTimeWindowEnd(String actualTimeWindowEnd) {
        this.actualTimeWindowEnd = actualTimeWindowEnd;
    }

    public static <C extends Collection> C create(Collection<GooglePlaceResponse.Prediction> items, C collection) {
        if (collection == null) return collection;
        if (items == null) return collection;

        for (GooglePlaceResponse.Prediction gpp : items)
            collection.add(create(gpp));
        return collection;
    }

    public static WayPoint create(GooglePlaceResponse.Prediction item) {
        WayPoint entity = new WayPoint();
        entity.setName(item.getDescription());
        entity.setPlaceId(item.getPlaceId());
        return entity;
    }

}
