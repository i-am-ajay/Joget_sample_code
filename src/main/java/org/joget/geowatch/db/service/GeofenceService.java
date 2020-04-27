package org.joget.geowatch.db.service;

import org.joget.geowatch.api.dto.WayPoint;
import org.joget.geowatch.db.dto.Geofence;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/25/2018
 * Time: 5:32 PM
 */
public interface GeofenceService {
    List<Geofence> list(String search) throws Exception;
    
    //new
    
    List<Geofence> listgeotype(String searchtype) throws Exception;

    String save(Geofence geofence) throws Exception;

    boolean update(Geofence geofence) throws Exception;

    boolean delete(String id) throws Exception;

//    Boolean isUsed(String geofenceId) throws Exception;

    List<WayPoint> listGoogleTips(String search, String googleApiKey) throws Exception;

    Long checkGeofenceName(String checkName) throws Exception;
}
