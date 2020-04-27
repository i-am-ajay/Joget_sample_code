package org.joget.geowatch.processing.analize.impl;

import com.google.maps.model.LatLng;
import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.processing.analize.Analyzer;
import org.joget.geowatch.processing.analize.dto.AnalyzeTripContext;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.processing.dto.LogData;
import org.joget.geowatch.type.EventType;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joget.geowatch.app.AppProperties.ANALYZE_GEOFENCE_TOLERANCE;
import static org.joget.geowatch.app.AppProperties.ANALYZE_ROUTE_TOLERANCE;
import static org.joget.geowatch.type.EventSubType.NONSENSE;
import static org.joget.geowatch.type.EventSubType.SOMETHING;
import static org.joget.geowatch.type.EventSubType.UNKNOWN;

public class GeofenceAnalyzer extends Analyzer {
    private static final String TAG = GeofenceAnalyzer.class.getSimpleName();

    @Override
    public void analyze(EventType eventType, AnalyzeTripContext tripContext, LogData logData) throws Exception {
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

    @Override
    public void analyze(EventType eventType, AnalyzeTripContext tripContext, Log log) throws Exception {

        switch (eventType) {
            case GEOFENCE_START:
                log.getAnalyzeResult().put(eventType, analyze(eventType, tripContext.getGeoList().get(0), log));
                break;
            case POD_SUBMIT:
            case GEOFENCE_FINISH:
                log.getAnalyzeResult().put(eventType, analyze(eventType,
                        tripContext.getGeoList().get(tripContext.getGeoList().size() - 1), log));
                break;
                
            case STOPPED_UNKNOWN_LOCATION:
            {
            	Double speed=0.0;
            	try {
					speed=Double.parseDouble(log.getSpeed());
				} catch (Exception e) {
					// TODO: handle exception
				}
            	System.out.println("SPEED=== STOPPED_UNKNOWN_LOCATION"+speed);
            	if (isEmpty(log.getLat()) || isEmpty(log.getLng()))
            	{
            		System.out.println("EMPTY LAT LONG STOPPED_UNKNOWN_LOCATION");
            		log.getAnalyzeResult().put(eventType, new AnalyzeResult(eventType, NONSENSE));
            	}
            	else if (log.getSpeed()==null||speed>1.0)
            	{
            		System.out.println("SPEED >1  STOPPED_UNKNOWN_LOCATION");
            		log.getAnalyzeResult().put(eventType, new AnalyzeResult(eventType, NONSENSE ));
            	}
            	else {                  
            		System.out.println("CHECKING PATH STOPPED_UNKNOWN_LOCATION");
            		 LatLng point = new LatLng(Double.parseDouble(log.getLat()), Double.parseDouble(log.getLng()));
            		  log.getAnalyzeResult().put(eventType, analyze(eventType,
                              tripContext, point));
            	}
            } 
            break;
            
            case STOPPED_BLACKLIST_LOCATION:
            {
            	Double speed=0.0;
            	try {
					speed=Double.parseDouble(log.getSpeed());
				} catch (Exception e) {
					// TODO: handle exception
				}
            	if (isEmpty(log.getLat()) || isEmpty(log.getLng()))
            	{
            		log.getAnalyzeResult().put(eventType, new AnalyzeResult(eventType, NONSENSE ));
            	}
            	else if (log.getSpeed()==null||speed>0.0)
            	{
            		log.getAnalyzeResult().put(eventType, new AnalyzeResult(eventType, NONSENSE ));
            	}
            	else {                  
            		 LatLng point = new LatLng(Double.parseDouble(log.getLat()), Double.parseDouble(log.getLng()));
             		
            		log.getAnalyzeResult().put(eventType, analyze_zones(eventType, tripContext.getBlackListedGeo(), point));
            	
            	}
            }   break;
            case STOPPED_REDZONE_LOCATION:
            {
            	Double speed=0.0;
            	try {
					speed=Double.parseDouble(log.getSpeed());
				} catch (Exception e) {
					// TODO: handle exception
				}
            	if (isEmpty(log.getLat()) || isEmpty(log.getLng()))
            	{
            		log.getAnalyzeResult().put(eventType, new AnalyzeResult(eventType, NONSENSE ));
            	}
            	else if (log.getSpeed()==null||speed>0.0)
            	{
            		log.getAnalyzeResult().put(eventType, new AnalyzeResult(eventType, NONSENSE ));
            	}
            	else {                  
            		 LatLng point = new LatLng(Double.parseDouble(log.getLat()), Double.parseDouble(log.getLng()));
             		
            		log.getAnalyzeResult().put(eventType, analyze_zones(eventType, tripContext.getAlertzonegeos(), point));
            	
            	}
            }   break;
            
            default:
                for (ExGeo geo : tripContext.getGeoList()) {
                    try {
                        log.getAnalyzeResult().put(eventType, analyze(eventType, geo, log));
                    } catch (Exception e) {
                        LogUtil.error(TAG, e, "ERROR.");
                    }
                }
        }
    }

    public AnalyzeResult analyze(EventType eventType, ExGeo geo, Log log) throws Exception {
        if (isEmpty(log.getLat()) || isEmpty(log.getLng()))
            return new AnalyzeResult(eventType, UNKNOWN, geo.getWp());

        LatLng point = new LatLng(Double.parseDouble(log.getLat()), Double.parseDouble(log.getLng()));
        if (geo.isWithin(point, ANALYZE_GEOFENCE_TOLERANCE))
            return new AnalyzeResult(eventType, SOMETHING, geo.getWp());

        return new AnalyzeResult(eventType, NONSENSE, geo.getWp());
    }
    
    
    private AnalyzeResult analyze(EventType eventType, AnalyzeTripContext tripContext, LatLng point) {

        for (ExGeo geo : tripContext.getGeoList()) {
            if (geo.isWithin(point, ANALYZE_GEOFENCE_TOLERANCE))
                return new AnalyzeResult(eventType, NONSENSE);
        }

        if (tripContext.getRoute().isWithin(point, ANALYZE_ROUTE_TOLERANCE))
            return new AnalyzeResult(eventType, NONSENSE);

        return new AnalyzeResult(eventType, SOMETHING);
    }
    
    
    private AnalyzeResult analyze_zones(EventType eventType, List<ExGeo> geos, LatLng point) {

        for (ExGeo geo : geos) {
        	System.out.println(" "+eventType.toString() +" ... "+geo.getWp().getLat()+" -- "+geo.getWp().getLng()+" ... "+point.lat+" .."+point.lng);
            if (geo.isWithin(point, ANALYZE_GEOFENCE_TOLERANCE))
                return new AnalyzeResult(eventType, SOMETHING);
        }

    

        return new AnalyzeResult(eventType, NONSENSE);
    }


}
