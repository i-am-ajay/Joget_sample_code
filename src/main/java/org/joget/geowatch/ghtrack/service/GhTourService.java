package org.joget.geowatch.ghtrack.service;

import org.joget.geowatch.db.dto.GhtVehicle;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.ght.net.dto.in.resp.error.TourError;
import org.joget.geowatch.util.UrlUtil;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/8/2018
 * Time: 1:06 PM
 */
public interface GhTourService {
    UrlUtil.RespResult<String, TourError> createOrUpdateTour(Trip trip, GhtVehicleInnerEntity ghtVehicle) throws Exception;

    List<GhtVehicle> listVehicle() throws Exception;
}
