package org.joget.geowatch.db.service;

import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.VehicleJson;

import java.util.Date;
import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 2:31 PM
 */
public interface GeoDataService {
    List<VehicleJson> listLiveTripGeoData(String tripId, Date dateStart, User currentUser) throws Exception;

    List<VehicleJson> listTripGeoData(String tripId, User currentUser) throws Exception;

}
