package org.joget.geowatch.api.dto.in.req;

import com.google.gson.annotations.SerializedName;
import org.joget.geowatch.type.NotifyType;
import org.joget.geowatch.type.ZoneType;
import org.joget.geowatch.type.WayPointType;

public class WayPointInReq {

    private String id;
    private String name;
    @SerializedName("type")
    private ZoneType zoneType;
    private WayPointType wayPointType;

    private Double lat;
    private Double lng;
    private Double radius;
    private String polygonHash;

    private Integer duration;
    private String timeWindowBegin;
    private String timeWindowEnd;
    @SerializedName("geofenceNotificationType")
    private NotifyType geofenceNotifyType;
    @SerializedName("endpointNotificationType")
    private NotifyType endpointNotifyType;

    private String address;
    private String description;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ZoneType getZoneType() {
        return zoneType;
    }

    public void setZoneType(ZoneType zoneType) {
        this.zoneType = zoneType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NotifyType getGeofenceNotifyType() {
        return geofenceNotifyType;
    }

    public void setGeofenceNotifyType(NotifyType geofenceNotifyType) {
        this.geofenceNotifyType = geofenceNotifyType;
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

    public String getPolygonHash() {
        return polygonHash;
    }

    public void setPolygonHash(String polygonHash) {
        this.polygonHash = polygonHash;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public NotifyType getEndpointNotifyType() {
        return endpointNotifyType;
    }

    public void setEndpointNotifyType(NotifyType endpointNotifyType) {
        this.endpointNotifyType = endpointNotifyType;
    }

    public WayPointType getWayPointType() {
        return wayPointType;
    }

    public void setWayPointType(WayPointType wayPointType) {
        this.wayPointType = wayPointType;
    }
}
