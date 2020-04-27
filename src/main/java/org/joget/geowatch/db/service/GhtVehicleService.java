package org.joget.geowatch.db.service;

import org.joget.geowatch.db.dto.GhtVehicle;

import java.util.Collection;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 4:26 PM
 */
public interface GhtVehicleService {
    GhtVehicle get(String ghtVehicleId) throws Exception;

    Collection<GhtVehicle> list() throws Exception;

    void create(Collection<GhtVehicle> ghtVehicles) throws Exception;

    void saveOrUpdate(GhtVehicle ghtVehicle) throws Exception;
}

