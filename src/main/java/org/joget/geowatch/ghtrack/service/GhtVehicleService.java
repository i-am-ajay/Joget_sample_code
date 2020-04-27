package org.joget.geowatch.ghtrack.service;

import org.joget.geowatch.db.dto.Vehicle;
import org.joget.geowatch.ght.net.dto.in.resp.GhtVehicleInResp;

import java.util.List;
import java.util.Map;

/**
 * Created by k.lebedyantsev
 * Date: 1/3/2018
 * Time: 4:54 PM
 */
public interface GhtVehicleService {
    Map<String, List<GhtVehicleInResp>> pullLiveTripGhtVehicleLog(Map<String, List<Vehicle>> lifeTripVehiclesMap) throws Exception;
}
