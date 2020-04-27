package org.joget.geowatch.db.dto;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;
import org.joget.geowatch.db.dto.type.VehicleType;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventSubType;
import org.joget.geowatch.type.EventType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 1/16/2018
 * Time: 12:23 PM
 */
@Entity
@Table(name = "app_fd_Event")
public class Event implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private String id;

//    @Id
//    @GeneratedValue(generator = "EventIdGenerator", strategy = GenerationType.SEQUENCE)
//    @GenericGenerator(name = "EventIdGenerator", strategy = "org.joget.geowatch.util.debug.db.generator.EventIdGenerator")
//    @Column(name = "id", nullable = false)
//    private String id;

    @Column(name = "dateCreated", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "dateModified", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

    @Column(name = "c_date", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_eventType", columnDefinition="longtext", updatable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_eventSubType", columnDefinition="longtext", updatable = false)
    private EventSubType eventSubType;

    @Column(name = "c_lat", updatable = false)
    @Type(type = "text")
    private String lat;

    @Column(name = "c_lng", updatable = false)
    @Type(type = "text")
    private String lng;

    @Column(name = "c_geofenceId")
    @Type(type = "text")
    private String geofenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_vehicleType", columnDefinition="longtext", updatable = false)
    private VehicleType vehicleType;

    @Transient
    private Trip trip;

    @Column(name = "c_tripId", updatable = false)
    @Type(type = "text")
    private String tripId;

    @Transient
    private VehicleInnerEntity vehicle;

    @Transient
    private WayPointInnerEntity wp;

    @Column(name = "c_vehicleId", updatable = false)
    @Type(type = "text")
    private String vehicleId;

    @Transient
    private GhtVehicleInnerEntity ghtVehicle;

    @Column(name = "c_ghtVehicleId", updatable = false)
    @Type(type = "text")
    private String ghtVehicleId;

    @Transient
    private Log log;

    @Column(name = "c_logId", updatable = false)
    @Type(type = "text")
    private String logId;

    public Event() {
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventSubType getEventSubType() {
        return eventSubType;
    }

    public void setEventSubType(EventSubType eventSubType) {
        this.eventSubType = eventSubType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public VehicleInnerEntity getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleInnerEntity vehicle) {
        this.vehicle = vehicle;
    }

    public GhtVehicleInnerEntity getGhtVehicle() {
        return ghtVehicle;
    }

    public void setGhtVehicle(GhtVehicleInnerEntity ghtVehicle) {
        this.ghtVehicle = ghtVehicle;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public WayPointInnerEntity getWp() {
        return wp;
    }

    public void setWp(WayPointInnerEntity wp) {
        this.wp = wp;
    }

    public static Event update(Event item, Log log, EventType eventType, EventSubType eventSubType, WayPointInnerEntity wp) {
        if (item == null) return null;
        if (eventType == null) return item;
        if (eventSubType == null) return item;

        AnalyzeResult analyzeResult = log.getAnalyzeResult().get(eventType);

        item.dateCreated = new Date();
        item.dateModified = new Date();
        item.eventType = eventType;
        item.eventSubType = eventSubType;
        item.wp = wp;

        if(log != null) {
            item.log = log;
            item.logId = log.getId();

            item.trip = log.getTrip();
            item.tripId = log.getTrip().getId();

            item.vehicle = log.getVehicle();
            item.vehicleId = log.getVehicle().getId();

            item.ghtVehicle = log.getGhtVehicle();
            item.ghtVehicleId = log.getGhtVehicle().getId();

            item.date = log.getDate();
            item.lat = log.getLat();
            item.lng = log.getLng();
        }

        switch (eventType) {
            case POD_SUBMIT:
            case GEOFENCE:
            case GEOFENCE_START:
            case GEOFENCE_FINISH:
                item.geofenceId = analyzeResult.getWp().getId();
                break;
        }
        return item;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", vehicleId='" + vehicleId + '\'' +
                ", tripId='" + tripId + '\'' +
                ", ghtVehicleId='" + ghtVehicleId + '\'' +
                ", logId='" + logId + '\'' +
                ", lng='" + lng + '\'' +
                ", eventType=" + eventType +
                ", date='" + date + '\'' +
                ", lat='" + lat + '\'' +
                '}';
    }
}
