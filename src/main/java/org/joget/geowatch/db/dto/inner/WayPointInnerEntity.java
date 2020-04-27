package org.joget.geowatch.db.dto.inner;

import org.joget.geowatch.api.dto.in.req.WayPointInReq;
import org.joget.geowatch.type.NotifyType;
import org.joget.geowatch.type.WayPointType;
import org.joget.geowatch.type.ZoneType;

import java.util.List;

public class WayPointInnerEntity {

    protected String id;
    protected String name;
    protected ZoneType zoneType;
    protected WayPointType wayPointType;

    protected Double lat;
    protected Double lng;
    protected Integer number;
    protected Double radius;
    protected String polygonHash;

    protected Integer duration;
    protected String timeWindowBegin;
    protected String timeWindowEnd;
    protected NotifyType geofenceNotifyType;
    protected NotifyType endpointNotifyType;

    protected String address;
    protected String description;

    private boolean reached;
    private Long delay;

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

    public ZoneType getZoneType() {
        return zoneType;
    }

    public void setZoneType(ZoneType zoneType) {
        this.zoneType = zoneType;
    }

    public WayPointType getWayPointType() {
        return wayPointType;
    }

    public void setWayPointType(WayPointType wayPointType) {
        this.wayPointType = wayPointType;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
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

    public NotifyType getGeofenceNotifyType() {
        return geofenceNotifyType;
    }

    public void setGeofenceNotifyType(NotifyType geofenceNotifyType) {
        this.geofenceNotifyType = geofenceNotifyType;
    }

    public NotifyType getEndpointNotifyType() {
        return endpointNotifyType;
    }

    public void setEndpointNotifyType(NotifyType endpointNotifyType) {
        this.endpointNotifyType = endpointNotifyType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isReached() {
        return reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public static List<WayPointInnerEntity> update(List<WayPointInnerEntity> itemList, List<WayPointInReq> wayPointInReqList) {
        if (itemList == null) return null;
        if (wayPointInReqList == null) return itemList;

        for (int i = 0; i < wayPointInReqList.size(); i++) {
            WayPointInReq wp = wayPointInReqList.get(i);
            itemList.add(update(new WayPointInnerEntity(), wp, i));
        }
        return itemList;
    }

    public static WayPointInnerEntity update(WayPointInnerEntity item, WayPointInReq wayPointInReq, Integer number) {
        if (item == null) return null;
        if (wayPointInReq == null) return null;

        item.id = wayPointInReq.getId();
        item.name = wayPointInReq.getName();
        item.zoneType = wayPointInReq.getZoneType();
        item.wayPointType = wayPointInReq.getWayPointType();

        item.lat = wayPointInReq.getLat();
        item.lng = wayPointInReq.getLng();
        item.number = number;
        item.radius = wayPointInReq.getRadius();
        item.polygonHash = wayPointInReq.getPolygonHash();

        item.duration = wayPointInReq.getDuration();
        item.timeWindowBegin = wayPointInReq.getTimeWindowBegin();
        item.timeWindowEnd = wayPointInReq.getTimeWindowEnd();
        item.geofenceNotifyType = wayPointInReq.getGeofenceNotifyType();
        item.endpointNotifyType = wayPointInReq.getEndpointNotifyType();

        item.description = wayPointInReq.getDescription();
        item.address = wayPointInReq.getAddress();
        return item;
    }
}
