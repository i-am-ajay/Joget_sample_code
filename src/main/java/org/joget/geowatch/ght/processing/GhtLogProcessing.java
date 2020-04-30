package org.joget.geowatch.ght.processing;

import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;
import org.joget.geowatch.db.service.LogService;
import org.joget.geowatch.ght.net.service.GhtVehicleNetService;
import org.joget.geowatch.ght.processing.dto.GhtLogResponse;
import org.joget.geowatch.processing.dto.LogData;
import org.joget.geowatch.processing.dto.VehicleProcessData;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.joget.geowatch.db.dto.type.VehicleType.HAULIER;
import static org.joget.geowatch.db.dto.type.VehicleType.RMO;
import static org.joget.geowatch.db.dto.type.VehicleType.TRAILER;

public class GhtLogProcessing {
    private static final String TAG = GhtLogProcessing.class.getSimpleName();

    protected LogService logService;
    protected GhtVehicleNetService netService;
    protected LogHandler logHandler;

    public GhtLogProcessing(LogService logService, GhtVehicleNetService netService, LogHandler logHandler) {
        this.logService = logService;
        this.netService = netService;
        this.logHandler = logHandler;
    }

    public VehicleProcessData process(Trip trip) throws Exception {

        VehicleProcessData vehicleProcessData = new VehicleProcessData(trip);
        getGhtLogResponse(vehicleProcessData);
        processResponse(vehicleProcessData);

        return vehicleProcessData;
    }

    public VehicleProcessData getGhtLogResponse(VehicleProcessData vehicleProcessData) throws Exception {
    	
    	
    	

        logService.updateLastTimeLogReqAndLog(vehicleProcessData);

        
        if (vehicleProcessData.getTrip().getHaulierGhtVehicle() != null)
        	       
            vehicleProcessData.addGhtResponse(HAULIER,
                    getGhtLogResponse(
                            vehicleProcessData.getTrip(),
                            vehicleProcessData.getTrip().getHaulierGhtVehicle(),
                            vehicleProcessData.getLastLogReqDate(HAULIER)));

        if (vehicleProcessData.getTrip().getHaulierTrailerGhtVehicle() != null)
            vehicleProcessData.addGhtResponse(TRAILER,
                    getGhtLogResponse(
                            vehicleProcessData.getTrip(),
                            vehicleProcessData.getTrip().getHaulierTrailerGhtVehicle(),
                            vehicleProcessData.getLastLogReqDate(TRAILER)));

        if (vehicleProcessData.getTrip().getRmoGhtVehicle() != null)
            vehicleProcessData.addGhtResponse(RMO,
                    getGhtLogResponse(
                            vehicleProcessData.getTrip(),
                            vehicleProcessData.getTrip().getRmoGhtVehicle(),
                            vehicleProcessData.getLastLogReqDate(RMO)));

        return vehicleProcessData;
    }

    protected GhtLogResponse getGhtLogResponse(Trip trip, GhtVehicleInnerEntity ghtVehicle, Date lastLogRrqDate) throws Exception {
       // System.out.println("getGhtLogResponse ghtVehicle == "+ghtVehicle);
    	if (ghtVehicle == null) return null;

        GhtLogResponse res = new GhtLogResponse(trip, ghtVehicle.getId(), lastLogRrqDate);
        res.setResponse(netService.pullGhtLogByGhtVehicleId(res.getGhtVehicleId(), res.getDate4newLogReq()));
        return res;
    }

    protected VehicleProcessData processResponse(VehicleProcessData vehicleProcessData) throws Exception {

        if (vehicleProcessData.getTrip().getHaulierGhtVehicle() != null)
            vehicleProcessData.addNewLogData(HAULIER,
                    processResponse(
                            vehicleProcessData.getTrip(),
                            vehicleProcessData.getTrip().getHaulierVehicle(),
                            vehicleProcessData.getTrip().getHaulierGhtVehicle(),
                            vehicleProcessData.getGhtResponse(HAULIER)));

        if (vehicleProcessData.getTrip().getHaulierTrailerGhtVehicle() != null)
            vehicleProcessData.addNewLogData(TRAILER,
                    processResponse(
                            vehicleProcessData.getTrip(),
                            vehicleProcessData.getTrip().getHaulierTrailerVehicle(),
                            vehicleProcessData.getTrip().getHaulierTrailerGhtVehicle(),
                            vehicleProcessData.getGhtResponse(TRAILER)));

        if (vehicleProcessData.getTrip().getRmoGhtVehicle() != null)
            vehicleProcessData.addNewLogData(RMO,
                    processResponse(
                            vehicleProcessData.getTrip(),
                            vehicleProcessData.getTrip().getRmoVehicle(),
                            vehicleProcessData.getTrip().getRmoGhtVehicle(),
                            vehicleProcessData.getGhtResponse(RMO)));

        return vehicleProcessData;
    }

    protected LogData processResponse(Trip trip, VehicleInnerEntity vehicle, GhtVehicleInnerEntity ghtVehicle, GhtLogResponse ghtLogResponse) throws Exception {
      
    //	System.out.println( "processResponse Before Condition ");
    	if (ghtLogResponse == null) return null;
        if (trip == null) return null;
        if (vehicle == null) return null;
        if (ghtVehicle == null) return null;
      //  System.out.println( "processResponse After Condition ");
        
        LogData logData = new LogData(trip.getId(), vehicle.getId(), ghtVehicle.getId(), ghtLogResponse.getDate4newLogReq());

        if (ghtLogResponse.getResponse().getResultObj() != null)
            logData.getLogList().addAll(logHandler.handleSample(
                    trip, vehicle, ghtVehicle,
                    ghtLogResponse.getResponse().getResultObj().getVehicleSamples().getSamples(),
                    ghtLogResponse.getResponse().getStatus(),
                    ghtLogResponse.getDate4newLogReq())
            );

        if (ghtLogResponse.getResponse().getErrorObj() != null)
            logData.getLogList().add(logHandler.handleError(
                    trip, vehicle, ghtVehicle,
                    ghtLogResponse.getResponse().getErrorObj(),
                    ghtLogResponse.getResponse().getStatus(),
                    ghtLogResponse.getDate4newLogReq())
            );

        logData.setLogList(sortAsc(logData.getLogList()));
        return logData;
    }

    protected List<Log> sortAsc(List<Log> list) {
        Collections.sort(list, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        return list;
    }
}
