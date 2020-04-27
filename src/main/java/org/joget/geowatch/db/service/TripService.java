package org.joget.geowatch.db.service;

import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.out.resp.TripOutResp;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.type.TripLifeStateType;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:53 PM
 */
public interface TripService {
    void update(Trip trip) throws Exception;

    List<Trip> getLiveTrip() throws Exception;

    Trip getTrip(String id) throws Exception;

    List<TripOutResp> listTrips(User currentUser, TripLifeStateType liveState) throws Exception;

    TripOutResp getLifeTripDetails(User user, String id) throws Exception;

    void transferTripToLife(int timeBeforeLive, TimeUnit unit) throws Exception;

    void update(String tripId, String haulierTripHistorySnapshot, String trailerTripHistorySnapshot, String rmoTripHistorySnapshot) throws Exception;

    TripOutResp getLifeTripByC__id(User user, String tripId) throws Exception;
}
