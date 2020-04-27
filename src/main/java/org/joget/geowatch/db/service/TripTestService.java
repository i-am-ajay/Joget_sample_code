package org.joget.geowatch.db.service;

import org.joget.geowatch.api.dto.out.resp.TripTestOutResp;

public interface TripTestService {
    TripTestOutResp sensors(String tripId) throws Exception;
}
