package org.joget.geowatch.db.dto;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joget.geowatch.UiString;
import org.joget.geowatch.api.dto.out.resp.NotifyOutResp;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;
import org.joget.geowatch.db.dto.type.NotifyResolveStatusType;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventSubType;
import org.joget.geowatch.type.EventType;
import org.joget.geowatch.type.NotifyType;

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
import java.util.concurrent.TimeUnit;

import static org.joget.geowatch.UiString.DELAY_ISSUE_FINISH_WP;
import static org.joget.geowatch.UiString.DELAY_ISSUE_START_WP;
import static org.joget.geowatch.UiString.DOOR_ISSUE;
import static org.joget.geowatch.UiString.GHT_ISSUE;
import static org.joget.geowatch.UiString.PANIC_ISSUE;
import static org.joget.geowatch.UiString.POD_ISSUE;
import static org.joget.geowatch.UiString.ROUTE_ISSUE;
import static org.joget.geowatch.UiString.ROUTE_RESOLVED;
import static org.joget.geowatch.UiString.WP_ENTERED;
import static org.joget.geowatch.UiString.WP_LEFT;
import static org.joget.geowatch.db.dto.type.NotifyResolveStatusType.NEW;
import static org.joget.geowatch.type.EventSubType.NONSENSE;
import static org.joget.geowatch.type.EventSubType.SOMETHING;
import static org.joget.geowatch.type.NotifyType.ALERT;
import static org.joget.geowatch.type.NotifyType.NOTHING;
import static org.joget.geowatch.type.NotifyType.NOTICE;

/**
 * Created by k.lebedyantsev
 * Date: 2/24/2018
 * Time: 9:53 AM
 */
@Entity
@Table(name = "app_fd_Notify")
public class Notify implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private String id;

//    @Id
//    @GeneratedValue(generator = "NotifyIdGenerator", strategy = GenerationType.SEQUENCE)
//    @GenericGenerator(name = "NotifyIdGenerator", strategy = "org.joget.geowatch.util.debug.db.generator.NotifyIdGenerator")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "c_notifyType", columnDefinition="longtext", updatable = false)
    private NotifyType notifyType;

    @Transient
    protected Event event1;

    @Column(name = "c_event_1_id", updatable = false)
    @Type(type = "text")
    private String event1Id;

    @Transient
    protected Event event2;

    @Column(name = "c_event_2_id")
    @Type(type = "text")
    private String event2Id;

    @Transient
    protected Log log1;

    @Column(name = "c_log_1_id")
    @Type(type = "text")
    private String log1Id;

    @Transient
    protected Log log2;

    @Column(name = "c_log_2_id")
    @Type(type = "text")
    private String log2Id;

    @Column(name = "c_description")
    @Type(type = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "c_resolveStatus", columnDefinition="longtext")
    private NotifyResolveStatusType status;

    @Column(name = "c_note")
    @Type(type = "text")
    private String note;

    @Transient
    protected Trip trip;

    @Column(name = "c_tripId", updatable = false)
    @Type(type = "text")
    private String tripId;

    @Transient
    private GhtVehicleInnerEntity ghtVehicle;

    @Column(name = "c_ghtVehicleId", updatable = false)
    @Type(type = "text")
    private String ghtVehicleId;

    @Column(name = "c_handler")
    @Type(type = "text")
    private String handler;

    @Column(name = "c_handleDate", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date handleDate;

    @Column(name = "c_doorNumber")
    @Type(type = "text")
    private String doorNumber;

    @Transient
    private VehicleInnerEntity vehicle;

    @Column(name = "c_vehicleId", updatable = false)
    @Type(type = "text")
    private String vehicleId;

    @Column(name = "c_duration")
    @Type(type = "long")
    private Long duration;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public NotifyType getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(NotifyType notifyType) {
        this.notifyType = notifyType;
    }

    public Event getEvent1() {
        return event1;
    }

    public void setEvent1(Event event1) {
        this.event1 = event1;
    }

    public String getEvent1Id() {
        return event1Id;
    }

    public void setEvent1Id(String event1Id) {
        this.event1Id = event1Id;
    }

    public Event getEvent2() {
        return event2;
    }

    public void setEvent2(Event event2) {
        this.event2 = event2;
    }

    public String getEvent2Id() {
        return event2Id;
    }

    public void setEvent2Id(String event2Id) {
        this.event2Id = event2Id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NotifyResolveStatusType getStatus() {
        return status;
    }

    public void setStatus(NotifyResolveStatusType status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public GhtVehicleInnerEntity getGhtVehicle() {
        return ghtVehicle;
    }

    public void setGhtVehicle(GhtVehicleInnerEntity ghtVehicle) {
        this.ghtVehicle = ghtVehicle;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Date getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(Date handleDate) {
        this.handleDate = handleDate;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(String doorNumber) {
        this.doorNumber = doorNumber;
    }

    public VehicleInnerEntity getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleInnerEntity vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public static Notify update(Notify item, Event event) throws Exception {
        if (item == null) return null;

        item.eventType = event.getEventType();
        item.eventSubType = event.getEventSubType();

        item.notifyType = getNotifyType(event);
        item.description = getDesc(event);

        item.dateCreated = new Date();
        item.dateModified = new Date();
        item.date = event.getDate();
        item.event1 = event;
        item.event1Id = event.getId();
        item.log1 = event.getLog();
        item.log1Id = event.getLog().getId();

        item.event2 = null;
        item.event2Id = null;
        item.log2 = null;
        item.log2Id = null;
        item.status = NEW;
        item.note = null;

        item.trip = event.getTrip();
        item.tripId = event.getTripId();

        item.vehicle = event.getVehicle();
        item.vehicleId = event.getVehicleId();

        item.ghtVehicle = event.getGhtVehicle();
        item.ghtVehicleId = event.getGhtVehicleId();

        item.handler = null;
        item.handleDate = null;
//        item.doorNumber
//        item.duration

        return item;
    }

    public static Notify update2(Notify item, Event event) throws Exception {
        if (item == null) return null;

        item.dateCreated = new Date();
        item.dateModified = new Date();
        item.event2 = event;
        item.event2Id = event.getId();
        item.log2 = event.getLog();
        item.log2Id = event.getLog().getId();

        long tDuration = event.getDate().getTime() - item.date.getTime();
        if (tDuration > 0) item.duration = TimeUnit.MILLISECONDS.toSeconds(tDuration);

        return item;
    }

    protected static NotifyType getNotifyType(Event event) throws Exception {
        AnalyzeResult analyzeResult = event.getLog().getAnalyzeResult().get(event.getEventType());

        EventType et = event.getEventType();
        EventSubType st = event.getEventSubType();

        if (SOMETHING != st && NONSENSE != st)
            throw new IllegalArgumentException("Can't resolve eventSubType: " + event.getEventSubType());

        switch (et) {
            case GHT_NET:
                if (SOMETHING == st) return ALERT;
                if (NONSENSE == st) return NOTHING;

            case DOOR1:
            case DOOR2:
            case DOOR3:
            case DOOR4:
                if (SOMETHING == st) return ALERT;
                if (NONSENSE == st) return NOTHING;

            case ROUTE:
                if (SOMETHING == st) return ALERT;
                if (NONSENSE == st) return NOTICE;

            case GEOFENCE:
                if (SOMETHING == st) return NOTICE;
                if (NONSENSE == st) return NOTICE;

            case GEOFENCE_START:
            case GEOFENCE_FINISH:
                if (SOMETHING == st) return
                        analyzeResult.getWp().getEndpointNotifyType() != null
                                ? analyzeResult.getWp().getEndpointNotifyType()
                                : NOTICE;
                if (NONSENSE == st) return NOTICE;

            case DELAY_START:
            case DELAY_FINISH:
                if (SOMETHING == st) return NOTICE;
                if (NONSENSE == st) return NOTICE;

            case POD_SUBMIT:
                if (SOMETHING == st) return ALERT;
                if (NONSENSE == st) return NOTHING;
            case STOPPED_UNKNOWN_LOCATION:
            case STOPPED_BLACKLIST_LOCATION:
            case STOPPED_REDZONE_LOCATION:
                if (SOMETHING == st) return ALERT;
                if (NONSENSE == st) return NOTHING;
            default:
                throw new IllegalArgumentException("Can't resolve eventType: " + event.getEventType());
        }
    }

    protected static String getDesc(Event event) throws Exception {

        EventType et = event.getEventType();
        EventSubType st = event.getEventSubType();
        AnalyzeResult analyzeResult = event.getLog().getAnalyzeResult().get(event.getEventType());
        switch (et) {
            case GHT_NET:
                if (SOMETHING == st) return String.format(GHT_ISSUE);
                if (NONSENSE == st) return "";

            case DOOR1:
            case DOOR2:
                if (SOMETHING == st) return String.format(PANIC_ISSUE,
                        event.getLog().getVehicle().getType(),
                        event.getLog().getGhtVehicle().getId());
                if (NONSENSE == st) return "";

            case DOOR3:
            case DOOR4:
                if (SOMETHING == st) return String.format(DOOR_ISSUE,
                        event.getLog().getVehicle().getType(),
                        event.getLog().getGhtVehicle().getId());
                if (NONSENSE == st) return "";

            case ROUTE:
                if (SOMETHING == st) return String.format(ROUTE_ISSUE,
                        event.getLog().getGhtVehicle().getId());
                if (NONSENSE == st) return String.format(ROUTE_RESOLVED,
                        event.getLog().getGhtVehicle().getId());

            case GEOFENCE:
            case GEOFENCE_START:
            case GEOFENCE_FINISH:
                if (SOMETHING == st) return String.format(WP_ENTERED,
                        event.getLog().getGhtVehicle().getId(),
                        analyzeResult.getWp().getName());
                if (NONSENSE == st) return String.format(WP_LEFT,
                        event.getLog().getGhtVehicle().getId(),
                        analyzeResult.getWp().getName());

            case DELAY_START:
                if (SOMETHING == st) return String.format(DELAY_ISSUE_START_WP,
                        event.getLog().getGhtVehicle().getId());
                if (NONSENSE == st) return "";

            case DELAY_FINISH:
                if (SOMETHING == st) return String.format(DELAY_ISSUE_FINISH_WP,
                        event.getLog().getGhtVehicle().getId());
                if (NONSENSE == st) return "";

            case POD_SUBMIT:
                if (SOMETHING == st) return String.format(POD_ISSUE);
                if (NONSENSE == st) return "";
            case STOPPED_UNKNOWN_LOCATION:
                if (SOMETHING == st) return String.format(UiString.UNKNOWN_LOCATION_ISSUE);
                if (NONSENSE == st) return "";
            case STOPPED_BLACKLIST_LOCATION:
                if (SOMETHING == st) return String.format(UiString.BLACKLISTED_ZONE_ISSUE);
                if (NONSENSE == st) return "";
            case STOPPED_REDZONE_LOCATION:
                if (SOMETHING == st) return String.format(UiString.RED_ZONE_ISSUE);
                if (NONSENSE == st) return "";
            default:
                throw new IllegalArgumentException("Can't resolve eventSubType: " + event.getEventSubType());
        }
    }

    @Override
    public String toString() {
        return "Notify{" +
                "id='" + id + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", date=" + date +
                ", eventType=" + eventType +
                ", eventSubType=" + eventSubType +
                ", notifyType=" + notifyType +
                ", event1Id='" + event1Id + '\'' +
                ", event2Id='" + event2Id + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", note='" + note + '\'' +
                ", tripId='" + tripId + '\'' +
                ", ghtVehicleId='" + ghtVehicleId + '\'' +
                ", handler='" + handler + '\'' +
                ", handleDate=" + handleDate +
                ", doorNumber='" + doorNumber + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
