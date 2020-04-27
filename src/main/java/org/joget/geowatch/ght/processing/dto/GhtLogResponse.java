package org.joget.geowatch.ght.processing.dto;

import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.ght.net.dto.in.resp.log.LogGhtVehicleInResp;
import org.joget.geowatch.ght.net.dto.in.resp.error.GhtVehicleError;
import org.joget.geowatch.util.UrlUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.apache.http.HttpStatus.SC_OK;
import static org.joget.geowatch.util.DateUtil.getJogetDate;

public class GhtLogResponse {

    protected Trip trip;
    protected String ghtVehicleId;
    protected Date date4newLogReq;
    protected UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError> response;

    public GhtLogResponse(Trip trip, String ghtVehicleId, Date lastLogReqDate) throws Exception {
        this.trip = trip;
        this.ghtVehicleId = ghtVehicleId;
        this.date4newLogReq = lastLogReqDate != null
                ? new Date(lastLogReqDate.getTime() + TimeUnit.SECONDS.toMillis(1))
                : getJogetDate(trip.getStartDateTime());
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }

    public Date getDate4newLogReq() {
        return date4newLogReq;
    }

    public void setDate4newLogReq(Date date4newLogReq) {
        this.date4newLogReq = date4newLogReq;
    }

    public UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError> getResponse() {
        return response;
    }

    public void setResponse(UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError> response) {
        this.response = response;
    }
}
