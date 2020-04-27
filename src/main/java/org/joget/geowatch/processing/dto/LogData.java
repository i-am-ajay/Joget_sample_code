package org.joget.geowatch.processing.dto;

import org.joget.geowatch.db.dto.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogData {
    protected String tripId;
    protected String vehicleId;
    protected String ghtVehicleId;
    protected Date lastDateLog;
    protected List<Log> logList = new ArrayList<>();

    public LogData(String tripId, String vehicleId, String ghtVehicleId, Date lastDateLog) {
        this.tripId = tripId;
        this.vehicleId = vehicleId;
        this.ghtVehicleId = ghtVehicleId;
        this.lastDateLog = lastDateLog;
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

    public Date getLastDateLog() {
        return lastDateLog;
    }

    public void setLastDateLog(Date lastDateLog) {
        this.lastDateLog = lastDateLog;
    }

    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }
}
