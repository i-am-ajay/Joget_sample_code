package org.joget.geowatch.db.service;

import org.joget.geowatch.api.dto.out.resp.VehicleOutResp;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.Vehicle;
import org.joget.geowatch.db.dto.type.VehicleType;

import java.util.List;
import java.util.Map;

/**
 * Created by k.lebedyantsev
 * Date: 2/13/2018
 * Time: 5:34 PM
 */
public interface VehicleService {
//    Map<String, List<Vehicle>> selectLiveTrip() throws Exception;

    VehicleOutResp getTripVehicle(String tripId, VehicleType vehicleType) throws Exception;

//    List<Vehicle> selectVehicleByTrip(Trip trip) throws Exception;
}
