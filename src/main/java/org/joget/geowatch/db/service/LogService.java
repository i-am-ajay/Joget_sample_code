package org.joget.geowatch.db.service;

import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.LogJson;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.processing.dto.VehicleProcessData;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 2/13/2018
 * Time: 12:19 PM
 */
public interface LogService {
    void save(Collection<Log> logCollection);

    String save(Log log) throws Exception;

    Date getLastLogDate(String lifeTripId) throws Exception;

    List<LogJson> list(String tripId, Date date, User user, Integer offset, Integer limit) throws Exception;
    
    
    Log getLastLogDetails(String tripId,String vehicleId) throws Exception ;

    LogJson get(String idLog, User currentUser) throws Exception;
    
    boolean checkActiveRecords(String tripId,String vehicleId);

    VehicleProcessData updateLastTimeLogReqAndLog(VehicleProcessData vehicleProcessData) throws Exception;
}
