package org.joget.geowatch.processing.dto;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.type.VehicleType;
import org.joget.geowatch.ght.processing.dto.GhtLogResponse;

import java.util.Date;

public class VehicleProcessData {

    protected Trip trip;
    protected Log[] lastLogArr = new Log[3];
    protected Date[] lastLogReqDate = new Date[3];
    protected GhtLogResponse[] ghtResponseArr = new GhtLogResponse[3];
    protected LogData[] newLogDataArr = new LogData[3];
    protected EventData[] newEventDataArr = new EventData[3];
    protected NotifyData[] newNotifyDataArr = new NotifyData[3];

//    public enum TripVehicleType {
//        RMO, HAULIER, TRAILER
//        HAULER_VEHICLE,
//        HAULER_TRAILER,
//        RMO_VEHICLE
//    }

    public VehicleProcessData(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public void addLastLog(VehicleType tripVehicleType, Log log) {
    	LogUtil.debug(this.getClass().getName(),"LOG for Saving -- Trip"+this.trip.getId());
    	if(log!=null)
    	{
    		LogUtil.debug(this.getClass().getName(),"LOG for Saving --"+log);
    		LogUtil.debug(this.getClass().getName(),"LOG for Saving -- Vehicle"+ log.getVehicleId());
    		LogUtil.debug(this.getClass().getName(),"LOG for Saving -- GHTVehicle"+ log.getGhtVehicleId());
    	}
    	lastLogArr[tripVehicleType.ordinal()] = log;
    }

    public Log getLastLog(VehicleType tripVehicleType) {
        return lastLogArr[tripVehicleType.ordinal()];
    }

    public Log[] getLastLogArr() {
        return lastLogArr;
    }

    public void addGhtResponse(VehicleType tripVehicleType, GhtLogResponse response) {
    	//System.out.println("RESPNSE FOR THE TRIP "+tripVehicleType);
    	//System.out.println(response);
        ghtResponseArr[tripVehicleType.ordinal()] = response;
    }

    public GhtLogResponse getGhtResponse(VehicleType tripVehicleType) {
        return ghtResponseArr[tripVehicleType.ordinal()];
    }

    public GhtLogResponse[] getGhtResponseArr() {
        return ghtResponseArr;
    }

    public void addNewLogData(VehicleType tripVehicleType, LogData logData) {
    	//System.out.println("added Logs for "+tripVehicleType);
    	//System.out.println(logData);
        newLogDataArr[tripVehicleType.ordinal()] = logData;
    }

    public LogData getNewLogData(VehicleType tripVehicleType) {
        return newLogDataArr[tripVehicleType.ordinal()];
    }

    public LogData[] getNewLogDataArr() {
        return newLogDataArr;
    }


    public void addNewEventData(VehicleType tripVehicleType, EventData eventDate) {
        newEventDataArr[tripVehicleType.ordinal()] = eventDate;
    }

    public EventData getNewEventData(VehicleType tripVehicleType) {
        return newEventDataArr[tripVehicleType.ordinal()];
    }

    public EventData[] getNewEventDataArr() {
        return newEventDataArr;
    }


    public void addNewNotifyData(VehicleType tripVehicleType, NotifyData eventDate) {
        newNotifyDataArr[tripVehicleType.ordinal()] = eventDate;
    }

    public NotifyData getNewNotifyData(VehicleType tripVehicleType) {
        return newNotifyDataArr[tripVehicleType.ordinal()];
    }

    public NotifyData[] getNewNotifyDataArr() {
        return newNotifyDataArr;
    }


    public void addLastLogReqDate(VehicleType tripVehicleType, Date date) {
        lastLogReqDate[tripVehicleType.ordinal()] = date;
    }

    public Date getLastLogReqDate(VehicleType tripVehicleType) {
        return lastLogReqDate[tripVehicleType.ordinal()];
    }

    public Date[] getLastLogReqDateArr() {
        return lastLogReqDate;
    }
}
