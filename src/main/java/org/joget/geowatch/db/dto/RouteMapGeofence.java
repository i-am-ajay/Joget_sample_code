package org.joget.geowatch.db.dto;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joget.geowatch.api.dto.in.req.WayPointInReq;
import org.joget.geowatch.type.NotifyType;
import org.joget.geowatch.type.ZoneType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static org.joget.geowatch.type.WayPointType.*;
import static org.joget.geowatch.util.DateUtil.getFeApiDate;

/**
 * Created by k.lebedyantsev
 * Date: 1/20/2018
 * Time: 12:35 PM
 */
@Entity
@Table(name = "app_fd_RouteMap_Geofence")
public class RouteMapGeofence implements Serializable {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "dateCreated", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "dateModified", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

    @Type(type = "text")
    @Column(name = "c_routeMapId")
    private String routeMapId;

    @Type(type = "text")
    @Column(name = "c_geofenceId")
    private String geofenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_geofenceNotifyType", columnDefinition="longtext")
    private NotifyType geofenceNotifyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_endpointNotifyType", columnDefinition="longtext")
    private NotifyType endpointNotifyType;

    @Type(type = "text")
    @Column(name = "c_number")
    private String number;

    @Type(type = "text")
    @Column(name = "c_duration")
    private String duration;

    @Column(name = "c_name")
    @Type(type = "text")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_type", columnDefinition="longtext")
    private ZoneType zoneType;

    @Type(type = "text")
    @Column(name = "c_lat")
    private Double lat;

    @Type(type = "text")
    @Column(name = "c_lng")
    private Double lng;

    @Column(name = "c_address")
    @Type(type = "text")
    private String address;

    @Type(type = "text")
    @Column(name = "c_polygonHash")
    private String polygonHash;

    @Type(type = "text")
    @Column(name = "c_radius")
    private Double radius;

    @Column(name = "c_description")
    @Type(type = "text")
    private String description;


    @Column(name = "c_timeWindowBegin", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeWindowBegin;

    @Column(name = "c_timeWindowEnd", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeWindowEnd;

    @Column(name = "c_actualTimeWindowBegin", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualTimeWindowBegin;

    @Column(name = "c_actualTimeWindowEnd", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualTimeWindowEnd;

    public RouteMapGeofence() {
    }

    public RouteMapGeofence(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getRouteMapId() {
        return routeMapId;
    }

    public void setRouteMapId(String routeMapId) {
        this.routeMapId = routeMapId;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeWindowBegin() {
        return timeWindowBegin;
    }

    public void setTimeWindowBegin(Date timeWindowBegin) {
        this.timeWindowBegin = timeWindowBegin;
    }

    public Date getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(Date timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }

    public Date getActualTimeWindowBegin() {
        return actualTimeWindowBegin;
    }

    public void setActualTimeWindowBegin(Date actualTimeWindowBegin) {
        this.actualTimeWindowBegin = actualTimeWindowBegin;
    }

    public Date getActualTimeWindowEnd() {
        return actualTimeWindowEnd;
    }

//    public void setActualTimeWindowEnd(Date actualTimeWindowEnd) {
//        this.actualTimeWindowEnd = actualTimeWindowEnd;
//    }
//
//    public static List<RouteMapGeofence> update(
//            List<RouteMapGeofence> itemList, List<WayPointInReq> wayPointList, String routMapId) throws Exception {
//
//        if (itemList == null) return null;
//        if (wayPointList == null) return itemList;
//
//        for (int i = 0; i < wayPointList.size(); i++)
//            itemList.add(RouteMapGeofence.update(new RouteMapGeofence(), wayPointList.getByRouteMapId(i), routMapId, i));
//
//        return itemList;
//    }

    public static RouteMapGeofence update(
            RouteMapGeofence item, WayPointInReq wayPoint, String routMapId, Integer number) throws Exception {

        if (item == null) return null;
        if (wayPoint == null) return item;

        if (START == wayPoint.getWayPointType()
                || FINISH == wayPoint.getWayPointType()
                || GEOFENCE == wayPoint.getWayPointType()) {

            item.dateModified = new Date();
            item.geofenceId = wayPoint.getId();
            item.routeMapId = routMapId;
            item.number = number + "";

            item.geofenceNotifyType = wayPoint.getGeofenceNotifyType();
            item.endpointNotifyType = wayPoint.getEndpointNotifyType();

            item.actualTimeWindowBegin = getFeApiDate(wayPoint.getTimeWindowBegin());
            item.actualTimeWindowEnd = getFeApiDate(wayPoint.getTimeWindowEnd());

            item.duration = wayPoint.getDuration() == null ? null : wayPoint.getDuration()+"";

            item.lat = wayPoint.getLat();
            item.lng = wayPoint.getLng();
            item.polygonHash = wayPoint.getPolygonHash();
            item.radius = wayPoint.getRadius();
        }
        return item;
    }
}
