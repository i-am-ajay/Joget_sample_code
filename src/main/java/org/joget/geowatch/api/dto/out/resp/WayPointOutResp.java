package org.joget.geowatch.api.dto.out.resp;

import com.google.gson.annotations.SerializedName;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;
import org.joget.geowatch.type.NotifyType;
import org.joget.geowatch.type.WayPointType;
import org.joget.geowatch.type.ZoneType;

public class WayPointOutResp {

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

	/*
	 * public Long getRadius() { return radius; }
	 * 
	 * public void setRadius(Long radius) { this.radius = radius; }
	 */
    
    
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


    public static WayPointOutResp[] update(WayPointOutResp[] itemArr, WayPointInnerEntity[] wayPointDbsArr) throws Exception {
        if (wayPointDbsArr == null) return itemArr;
        if (itemArr == null) return null;

        for (int i = 0; i < wayPointDbsArr.length; i++)
            itemArr[i] = update(new WayPointOutResp(), wayPointDbsArr[i], i);
        return itemArr;
    }

    public static WayPointOutResp update(WayPointOutResp item, WayPointInnerEntity wayPoint, int number) {
        if (wayPoint == null) return item;
        if (item == null) return null;

        item.id = wayPoint.getId();
        item.name = wayPoint.getName();
        item.zoneType = wayPoint.getZoneType();
        item.wayPointType = wayPoint.getWayPointType();

        item.lat = wayPoint.getLat();
        item.lng = wayPoint.getLng();
        item.radius = wayPoint.getRadius();
        item.polygonHash = wayPoint.getPolygonHash();

        item.duration = wayPoint.getDuration();
        item.timeWindowBegin = wayPoint.getTimeWindowBegin();
        item.timeWindowEnd = wayPoint.getTimeWindowEnd();
        item.geofenceNotifyType = wayPoint.getGeofenceNotifyType();
        item.endpointNotifyType = wayPoint.getEndpointNotifyType();

        item.description = wayPoint.getDescription();
        item.address = wayPoint.getAddress();
        return item;
    }

//    public static WayPointOutResp create(WayPointOutResp item, RouteMapGeofence routeMapGeofence) throws Exception {
//        if (routeMapGeofence == null) return item;
//        if (item == null) return null;
//
//        item.id = routeMapGeofence.getGhtVehicleInResp();
//        item.lat = routeMapGeofence.getLat();
//        item.lng = routeMapGeofence.getLng();
//        item.radius = routeMapGeofence.getRadius();
//        item.polygonHash = routeMapGeofence.getPolygonHash();
//        item.timeWindowBegin = getUiStrDate(routeMapGeofence.getTimeWindowBegin());
//        item.timeWindowEnd = getUiStrDate(routeMapGeofence.getTimeWindowEnd());
//        item.endpointNotifyType = routeMapGeofence.getEndpointNotifyType();
//        item.geofenceNotifyType = routeMapGeofence.getGeofenceNotifyType();
//
//        return item;
//    }

//    public static WayPointOutResp[] create(WayPointOutResp[] itemArr, List<WayPointInnerEntity> wayPointList) {
//        if (wayPointList == null) return itemArr;
//        if (itemArr == null) return null;
//
//        for (WayPointInnerEntity wp : wayPointList)
//            itemArr[wp.getNumber()] = create(new WayPointOutResp(), wp);
//        return itemArr;
//    }
//

}
