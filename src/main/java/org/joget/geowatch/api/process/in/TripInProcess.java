package org.joget.geowatch.api.process.in;

import com.google.maps.model.LatLng;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.dto.Location;
import org.joget.geowatch.api.dto.VehicleJson;
import org.joget.geowatch.api.dto.out.resp.TripOutResp;
import org.joget.geowatch.db.dto.type.TripLifeStateType;
import org.joget.geowatch.db.service.GeoDataService;
import org.joget.geowatch.db.service.TripService;
import org.joget.geowatch.util.SnapshotUtil;

import java.util.ArrayList;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.http.HttpStatus.SC_OK;
import static org.joget.geowatch.app.AppProperties.PLUGIN_DEBUG_MODE;

public class TripInProcess {
    private static final String TAG = TripInProcess.class.getSimpleName();

    private TripService tripService;
    private GeoDataService geoDataService;

    public TripInProcess(TripService tripService, GeoDataService geoDataService) {
        this.tripService = tripService;
        this.geoDataService = geoDataService;
    }

    public HttpWrap get(HttpWrap httpWrap, User user, String tripId, TripLifeStateType liveState) throws Exception {
        //if (isBlank(id)) httpWrap.result(SC_OK, tripService.listTrips(user));
        //else httpWrap.result(SC_OK, tripService.getLifeTripDetails(id, user));
        if (PLUGIN_DEBUG_MODE) {
            LogUtil.info(TAG, "TripInProcess tripId: " + tripId);
            LogUtil.info(TAG, "TripInProcess liveState: " + liveState);
        }
        if (isNotEmpty(tripId)) {
            if (tripId.contains("TRIP")) {//do snip for life trip. joget limitation.i cann't set http request's type
                //please review trip process.joget's json tool doesn't allow set request type
                //it was fixed at joget v.6
                return post(httpWrap, user, tripId);
            } else {
                TripOutResp tripOutResp = tripService.getLifeTripDetails(user, tripId);
                return httpWrap.result(SC_OK, tripOutResp);
            }
        } else {
            List<TripOutResp> tripList = tripService.listTrips(user, liveState);
            return httpWrap.result(SC_OK, tripList);
        }
    }

    public HttpWrap post(HttpWrap httpWrap, User user, String tripId) throws Exception {
        if (!isEmpty(tripId)) {
            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "TripInProcess: " + tripId + ", post.");
            }
            TripOutResp tripOutResp = tripService.getLifeTripByC__id(user, tripId);
            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "TripInProcess: " + tripId + ", tripOutResp: " + tripOutResp);
            }

            List<VehicleJson> vehicles = geoDataService.listTripGeoData(tripOutResp.getId(), user);
            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "TripInProcess: " + tripId + ", vehicles: " + vehicles.size());
            }
            String haulierTripHistorySnapshot = null;
            String trailerTripHistorySnapshot = null;
            String rmoTripHistorySnapshot = null;
            for (VehicleJson vehicle : vehicles) {
                List<LatLng> tempList;
                if (PLUGIN_DEBUG_MODE) {
                    LogUtil.info(TAG, "TripInProcess: " + tripId + ", vehicle.getAllLocations(): " + vehicle.getAllLocations().size());
                }
                if (vehicle.getAllLocations() != null && vehicle.getAllLocations().size() > 0) {
                    tempList = new ArrayList<>();
                    for (Location location : vehicle.getAllLocations()) {
                        LatLng latLng = new LatLng(location.getLat(), location.getLng());
                        tempList.add(latLng);
                    }
                    LogUtil.info(TAG, "TripHistorySnapshot for: " + tripId + "; vehicle: " + vehicle.getType());
                    switch (vehicle.getType()) {
                        case HAULIER:
                            haulierTripHistorySnapshot = SnapshotUtil.snip(tempList);
                            break;
                        case RMO:
                            rmoTripHistorySnapshot = SnapshotUtil.snip(tempList);
                            break;
                        case TRAILER:
                            trailerTripHistorySnapshot = SnapshotUtil.snip(tempList);
                            break;
                    }
                }
            }
            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "TripInProcess: " + tripId + ", haulierTripHistorySnapshot != null : " + (haulierTripHistorySnapshot != null));
                LogUtil.info(TAG, "TripInProcess: " + tripId + ", trailerTripHistorySnapshot != null : " + (trailerTripHistorySnapshot != null));
                LogUtil.info(TAG, "TripInProcess: " + tripId + ", rmoTripHistorySnapshot != null : " + (rmoTripHistorySnapshot != null));
            }
            if (haulierTripHistorySnapshot != null
                    || trailerTripHistorySnapshot != null
                    || rmoTripHistorySnapshot != null) {
                tripService.update(tripOutResp.getId(), haulierTripHistorySnapshot, trailerTripHistorySnapshot, rmoTripHistorySnapshot);
            }
            return httpWrap.result(SC_OK, null);
        } else {
            return httpWrap.result(SC_BAD_REQUEST, null);
        }
    }
}
