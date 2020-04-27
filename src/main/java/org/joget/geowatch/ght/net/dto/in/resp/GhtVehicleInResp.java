package org.joget.geowatch.ght.net.dto.in.resp;

import org.joget.geowatch.ght.net.dto.in.resp.error.GhtVehicleError;
import org.joget.geowatch.ght.net.dto.in.resp.log.LogGhtVehicleInResp;

public class GhtVehicleInResp {
    private String tripId;
    private String vehicleId;
    private String ghtVehicleId;
    private LogGhtVehicleInResp result;
    private GhtVehicleError error;

    public GhtVehicleInResp(String tripId, String vehicleId, String ghtVehicleId, LogGhtVehicleInResp result, GhtVehicleError error) {
        this.tripId = tripId;
        this.vehicleId = vehicleId;
        this.ghtVehicleId = ghtVehicleId;
        this.result = result;
        this.error = error;
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

    public LogGhtVehicleInResp getResult() {
        return result;
    }

    public void setResult(LogGhtVehicleInResp result) {
        this.result = result;
    }

    public GhtVehicleError getError() {
        return error;
    }

    public void setError(GhtVehicleError error) {
        this.error = error;
    }
}
