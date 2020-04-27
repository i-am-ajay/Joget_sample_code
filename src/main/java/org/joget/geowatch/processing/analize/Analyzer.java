package org.joget.geowatch.processing.analize;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.service.GeofenceService;
import org.joget.geowatch.processing.analize.dto.AnalyzeTripContext;
import org.joget.geowatch.processing.analize.impl.DoorSensorAnalizer;
import org.joget.geowatch.processing.analize.impl.GeofenceAnalyzer;
import org.joget.geowatch.processing.analize.impl.GhtNetResultAnalizer;
import org.joget.geowatch.processing.analize.impl.RouteAnalyzer;
import org.joget.geowatch.processing.dto.LogData;
import org.joget.geowatch.processing.dto.VehicleProcessData;
import org.joget.geowatch.type.EventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.joget.geowatch.type.EventType.DOOR1;
import static org.joget.geowatch.type.EventType.DOOR2;
import static org.joget.geowatch.type.EventType.DOOR3;
import static org.joget.geowatch.type.EventType.DOOR4;
import static org.joget.geowatch.type.EventType.GEOFENCE;
import static org.joget.geowatch.type.EventType.GEOFENCE_FINISH;
import static org.joget.geowatch.type.EventType.GEOFENCE_START;
import static org.joget.geowatch.type.EventType.GHT_NET;
import static org.joget.geowatch.type.EventType.POD_SUBMIT;
import static org.joget.geowatch.type.EventType.*;

public abstract class Analyzer {
    private static final String TAG = Analyzer.class.getSimpleName();
    
    

    protected static final Map<EventType, Analyzer> ANALYZES = new HashMap<>();

    static {
        ANALYZES.put(POD_SUBMIT, new GeofenceAnalyzer());
        ANALYZES.put(GEOFENCE, new GeofenceAnalyzer());
        ANALYZES.put(GEOFENCE_START, new GeofenceAnalyzer());
        ANALYZES.put(GEOFENCE_FINISH, new GeofenceAnalyzer());
        ANALYZES.put(ROUTE, new RouteAnalyzer());
        ANALYZES.put(DOOR1, new DoorSensorAnalizer());
        ANALYZES.put(DOOR2, new DoorSensorAnalizer());
        ANALYZES.put(DOOR3, new DoorSensorAnalizer());
        ANALYZES.put(DOOR4, new DoorSensorAnalizer());
        ANALYZES.put(GHT_NET, new GhtNetResultAnalizer());
        ANALYZES.put(STOPPED_UNKNOWN_LOCATION, new GeofenceAnalyzer());
        ANALYZES.put(STOPPED_BLACKLIST_LOCATION, new GeofenceAnalyzer());
        ANALYZES.put(STOPPED_REDZONE_LOCATION, new GeofenceAnalyzer());
        
    }

    public static void analyze(VehicleProcessData vehicleProcessData,GeofenceService geofenceService) throws Exception {
    	
        Trip trip = vehicleProcessData.getTrip();
        AnalyzeTripContext tripContext = getTripContext(trip);
        
        tripContext.setBlackListed(geofenceService.listgeotype("BLACKLIST_ZONE"));
        tripContext.setAlertzones(geofenceService.listgeotype("ALERT_ZONE"));
        for (LogData logDate : vehicleProcessData.getNewLogDataArr()) {
            try {
                if (logDate != null) analyze(tripContext, logDate);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR. " +
                        "tripId: " + vehicleProcessData.getTrip().getId());
            }
        }
    }

    public static AnalyzeTripContext getTripContext(Trip trip) throws Exception {
        return AnalyzeTripContext.getInstance(trip);
    }

    public static void analyze(AnalyzeTripContext tripContext, LogData logData) {
        for (Map.Entry<EventType, Analyzer> entry : ANALYZES.entrySet()) {
            try {
                entry.getValue().analyze(entry.getKey(), tripContext, logData);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR.");
            }
        }
    }

    public void analyze(EventType eventType, AnalyzeTripContext tripContext, LogData logData) throws Exception{
        analyze(eventType, tripContext, logData.getLogList());
    }

    public void analyze(EventType eventType, AnalyzeTripContext tripContext, List<Log> logList) throws Exception {
        for (Log log : logList) {
            try {
                analyze(eventType, tripContext, log);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR.");
            }
        }
    }

    public abstract void analyze(EventType eventType, AnalyzeTripContext tripContext, Log log) throws Exception;
}
