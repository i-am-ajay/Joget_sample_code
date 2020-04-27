package org.joget.geowatch.db.dto;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joget.geowatch.db.dto.converter.AnalyzeResultConverter;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventSubType;
import org.joget.geowatch.type.EventType;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Size;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import static org.joget.geowatch.util.DateUtil.getFeApiStrDate;

/**
 * Created by k.lebedyantsev
 * Date: 1/16/2018
 * Time: 12:22 PM
 */
@Entity
@Table(name = "app_fd_Log", indexes= {@Index(name = "group_index", columnList = "c_date,c_tripId,c_vehicleId")})
public class Log implements Serializable {

    @Id
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private String id;

//    @Id
//    @GeneratedValue(generator = "LogIdGenerator", strategy = GenerationType.SEQUENCE)
//    @GenericGenerator(name = "LogIdGenerator", strategy = "org.joget.geowatch.util.debug.db.generator.LogIdGenerator")
//    @Column(name = "id", nullable = false)
//    private String id;

    @Column(name = "dateCreated", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "dateModified", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

    @Column(name = "c_serverDate", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date serverDate;

    @Column(name = "c_reqDate", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date reqDate;

    @Column(name = "c_date", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

//    @Column(name = "c_incomDateStr", updatable = false)
//    @Type(type = "text")
//    private String incomDateStr;
//
//    @Column(name = "c_dateStr", updatable = false)
//    @Type(type = "text")
//    private String dateStr;

    @Column(name = "c_tripId", updatable = false,length=250)

    private String tripId;

    @Column(name = "c_vehicleId", updatable = false,length=250)
    
    private String vehicleId;

    @Column(name = "c_ghtVehicleId", updatable = false)
    @Type(type = "text")
    private String ghtVehicleId;

    @Column(name = "c_ghtLogId", updatable = false)
    @Type(type = "text")
    private String ghtLogId;

    @Column(name = "c_lng", updatable = false)
    @Type(type = "text")
    private String lng;

    @Column(name = "c_lat", updatable = false)
    @Type(type = "text")
    private String lat;

    @Column(name = "c_speed", updatable = false)
    @Type(type = "text")
    private String speed;

    @Column(name = "c_shortDesc")
    @Type(type = "text")
    private String shortDesc;

    @Column(name = "c_longDesc")
    @Type(type = "text")
    private String longDesc;

    @Column(name = "c_ghtLogType", updatable = false)
    @Type(type = "text")
    private String ghtLogType;

    @Column(name = "c_batteryVoltage", updatable = false)
    @Type(type = "text")
    private String batteryVoltage;

    @Type(type = "true_false")
    @Column(name = "c_door1State", updatable = false)
    private Boolean door1State;

    @Type(type = "true_false")
    @Column(name = "c_door2State", updatable = false)
    private Boolean door2State;

    @Type(type = "true_false")
    @Column(name = "c_door3State", updatable = false)
    private Boolean door3State;

    @Type(type = "true_false")
    @Column(name = "c_door4State", updatable = false)
    private Boolean door4State;

    @Column(name = "c_tempSensor1", updatable = false)
    @Type(type = "text")
    private String tempSensor1;

    @Column(name = "c_tempSensor2", updatable = false)
    @Type(type = "text")
    private String tempSensor2;

    @Column(name = "c_tempSensor3", updatable = false)
    @Type(type = "text")
    private String tempSensor3;

    @Column(name = "c_tempSensor4", updatable = false)
    @Type(type = "text")
    private String tempSensor4;

    @Column(name = "c_tempSensor5", updatable = false)
    @Type(type = "text")
    private String tempSensor5;

    @Column(name = "c_tempSensor6", updatable = false)
    @Type(type = "text")
    private String tempSensor6;

    @Column(name = "c_tempSensor7", updatable = false)
    @Type(type = "text")
    private String tempSensor7;

    @Column(name = "c_tempSensor8", updatable = false)
    @Type(type = "text")
    private String tempSensor8;

    @Column(name = "c_tempSensor9", updatable = false)
    @Type(type = "text")
    private String tempSensor9;

    @Column(name = "c_tempSetpoint1", updatable = false)
    @Type(type = "text")
    private String tempSetpoint1;

    @Column(name = "c_tempSetpoint2", updatable = false)
    @Type(type = "text")
    private String tempSetpoint2;

    @Column(name = "c_tempSetpoint3", updatable = false)
    @Type(type = "text")
    private String tempSetpoint3;

    @Column(name = "c_ghtNetResult", updatable = false)
    @Type(type = "integer")
    private Integer ghtNetResult;

    @Convert(converter = AnalyzeResultConverter.class)
    @Column(name = "c_analyzeResult", columnDefinition="longtext", updatable = false)
    private Map<EventType, AnalyzeResult> analyzeResult;

    @Transient
    private Trip trip;

    @Transient
    private VehicleInnerEntity vehicle;

    @Transient
    private GhtVehicleInnerEntity ghtVehicle;

    public Log() {}

    public Log(Map<EventType, AnalyzeResult> analyzeResult) {
        this.analyzeResult = analyzeResult;
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

    public Date getServerDate() {
        return serverDate;
    }

    public void setServerDate(Date serverDate) {
        this.serverDate = serverDate;
    }

    public Date getReqDate() {
        return reqDate;
    }

    public void setReqDate(Date reqDate) {
        this.reqDate = reqDate;
    }

    public Date getDate() {
        return date;
    }

//    public String getIncomDateStr() {
//        return incomDateStr;
//    }
//
//    public void setIncomDateStr(String incomDateStr) {
//        this.incomDateStr = incomDateStr;
//    }

    public void setDate(Date date) {
        this.date = date;
//        try {
//            dateStr = getFeApiStrDate(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getGhtLogId() {
        return ghtLogId;
    }

    public void setGhtLogId(String ghtLogId) {
        this.ghtLogId = ghtLogId;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getGhtLogType() {
        return ghtLogType;
    }

    public void setGhtLogType(String ghtLogType) {
        this.ghtLogType = ghtLogType;
    }

    public String getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(String batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public Boolean getDoor1State() {
        return door1State;
    }

    public void setDoor1State(Boolean door1State) {
        this.door1State = door1State;
    }

    public Boolean getDoor2State() {
        return door2State;
    }

    public void setDoor2State(Boolean door2State) {
        this.door2State = door2State;
    }

    public Boolean getDoor3State() {
        return door3State;
    }

    public void setDoor3State(Boolean door3State) {
        this.door3State = door3State;
    }

    public Boolean getDoor4State() {
        return door4State;
    }

    public void setDoor4State(Boolean door4State) {
        this.door4State = door4State;
    }

    public String getTempSensor1() {
        return tempSensor1;
    }

    public void setTempSensor1(String tempSensor1) {
        this.tempSensor1 = tempSensor1;
    }

    public String getTempSensor2() {
        return tempSensor2;
    }

    public void setTempSensor2(String tempSensor2) {
        this.tempSensor2 = tempSensor2;
    }

    public String getTempSensor3() {
        return tempSensor3;
    }

    public void setTempSensor3(String tempSensor3) {
        this.tempSensor3 = tempSensor3;
    }

    public String getTempSensor4() {
        return tempSensor4;
    }

    public void setTempSensor4(String tempSensor4) {
        this.tempSensor4 = tempSensor4;
    }

    public String getTempSensor5() {
        return tempSensor5;
    }

    public void setTempSensor5(String tempSensor5) {
        this.tempSensor5 = tempSensor5;
    }

    public String getTempSensor6() {
        return tempSensor6;
    }

    public void setTempSensor6(String tempSensor6) {
        this.tempSensor6 = tempSensor6;
    }

    public String getTempSensor7() {
        return tempSensor7;
    }

    public void setTempSensor7(String tempSensor7) {
        this.tempSensor7 = tempSensor7;
    }

    public String getTempSensor8() {
        return tempSensor8;
    }

    public void setTempSensor8(String tempSensor8) {
        this.tempSensor8 = tempSensor8;
    }

    public String getTempSensor9() {
        return tempSensor9;
    }

    public void setTempSensor9(String tempSensor9) {
        this.tempSensor9 = tempSensor9;
    }

    public String getTempSetpoint1() {
        return tempSetpoint1;
    }

    public void setTempSetpoint1(String tempSetpoint1) {
        this.tempSetpoint1 = tempSetpoint1;
    }

    public String getTempSetpoint2() {
        return tempSetpoint2;
    }

    public void setTempSetpoint2(String tempSetpoint2) {
        this.tempSetpoint2 = tempSetpoint2;
    }

    public String getTempSetpoint3() {
        return tempSetpoint3;
    }

    public void setTempSetpoint3(String tempSetpoint3) {
        this.tempSetpoint3 = tempSetpoint3;
    }

    public Integer getGhtNetResult() {
        return ghtNetResult;
    }

    public void setGhtNetResult(Integer ghtNetResult) {
        this.ghtNetResult = ghtNetResult;
    }

    public Map<EventType, AnalyzeResult> getAnalyzeResult() {
        return analyzeResult;
    }

    public void setAnalyzeResult(Map<EventType, AnalyzeResult> analyzeResult) {
        this.analyzeResult = analyzeResult;
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

    public static Log update(Log log, EventType eventType, EventSubType eventSubType, WayPointInnerEntity wp) throws Exception {
        if(log == null) return null;
        log.getAnalyzeResult().put(eventType, new AnalyzeResult(eventType, eventSubType, wp));
        return log;
    }
}
