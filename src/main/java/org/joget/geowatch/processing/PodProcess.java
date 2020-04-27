package org.joget.geowatch.processing;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.Event;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.type.TripLifeStateType;
import org.joget.geowatch.db.dto.type.TripLifeSubStateType;
import org.joget.geowatch.db.service.TripService;
import org.joget.geowatch.processing.dto.EventData;
import org.joget.geowatch.processing.dto.VehicleProcessData;

import java.util.Date;
import java.util.List;

import static org.joget.geowatch.app.AppProperties.JOGET_PROCESS_TRIP_POD_ROUTE_ID;
import static org.joget.geowatch.app.AppProperties.PLUGIN_DEBUG_MODE;
import static org.joget.geowatch.db.dto.type.TripLifeStateType.COMPLETED;
import static org.joget.geowatch.db.dto.type.TripLifeStateType.LIVE;
import static org.joget.geowatch.db.dto.type.TripLifeSubStateType.POD;
import static org.joget.geowatch.type.EventSubType.SOMETHING;
import static org.joget.geowatch.type.EventType.POD_SUBMIT;
import static org.joget.geowatch.util.JogetUtil.completeActivity;
import static org.joget.geowatch.util.JogetUtil.updateJogetProccess;

public class PodProcess {
    private static final String TAG = PodProcess.class.getSimpleName();

    protected TripService tripService;

    public PodProcess(TripService tripService) {
        this.tripService = tripService;
    }

    public void process(VehicleProcessData vehicleProcessData) {
        try {
            if (checkPodEvent(vehicleProcessData))
                updateTrip4Pod(vehicleProcessData);
        } catch (Exception e) {
            LogUtil.error(TAG, e, "ERROR");
        }
    }

    protected static boolean checkPodEvent(VehicleProcessData vehicleProcessData) {
        Trip trip = vehicleProcessData.getTrip();
        TripLifeStateType lifeStateType = trip.getLiveState();
        TripLifeSubStateType lifeSubStateType = trip.getLiveSubState();
        String tripId = trip.getId();
        String c_Id = trip.getC__id();

        if (PLUGIN_DEBUG_MODE) {
            LogUtil.info(TAG, "PodProcess checkPodEvent tripId: " + tripId);
            LogUtil.info(TAG, "PodProcess checkPodEvent c__Id: " + c_Id);
            LogUtil.info(TAG, "PodProcess checkPodEvent lifeStateType: " + lifeStateType);
            LogUtil.info(TAG, "PodProcess checkPodEvent liveSubState: " + lifeSubStateType);
        }
        if ((lifeStateType == LIVE
                && lifeSubStateType == POD) || lifeStateType == COMPLETED) return false;
        for (EventData ed : vehicleProcessData.getNewEventDataArr()) {
            if (isHavePodEvent(ed))
                return true;
        }
        return false;
    }

    protected static boolean isHavePodEvent(EventData eventDate) {

        if (eventDate == null) {
            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "PodProcess isHavePodEvent: eventDate == null");
            }
            return false;
        }
        if (eventDate.getNewEventMap() == null) {
            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "PodProcess isHavePodEvent: eventDate.getNewEventMap() == null");
            }
            return false;
        }
        if (eventDate.getNewEventMap().get(POD_SUBMIT) == null) {
            LogUtil.info(TAG, "PodProcess isHavePodEvent: eventDate.getNewEventMap().get(POD_SUBMIT) == null");
            return false;
        }

        List<Event> podEventList = eventDate.getNewEventMap().get(POD_SUBMIT);
        for (Event e : podEventList) {
            if (e.getEventSubType() == SOMETHING)
                return true;
        }
        return false;
    }

    protected void updateTrip4Pod(VehicleProcessData vehicleProcessData) throws Exception {
        Trip trip = vehicleProcessData.getTrip();
        if (PLUGIN_DEBUG_MODE) {
            LogUtil.info(TAG, "PodProcess updateTrip4Pod _Id: " + trip.getC__id());
            LogUtil.info(TAG, "PodProcess updateTrip4Pod LiveState: " + trip.getLiveState());
            LogUtil.info(TAG, "PodProcess updateTrip4Pod LiveSubState: " + trip.getLiveSubState());
        }

        trip.setLiveState(LIVE);
        trip.setLiveSubState(POD);
        tripService.update(trip);

        updateJogetProccess(trip.getId(), "liveState", LIVE.name());
        updateJogetProccess(trip.getId(), "liveSubState", POD.name());
        completeActivity(trip.getId(), JOGET_PROCESS_TRIP_POD_ROUTE_ID);

    }
}
