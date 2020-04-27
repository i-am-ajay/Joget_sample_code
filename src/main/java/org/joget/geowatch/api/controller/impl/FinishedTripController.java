package org.joget.geowatch.api.controller.impl;

import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.process.in.TripInProcess;
import org.joget.geowatch.db.service.TripService;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.joget.geowatch.app.AppProperties.PLUGIN_DEBUG_MODE;
import static org.joget.geowatch.db.dto.type.TripLifeStateType.COMPLETED;

/**
 * Created by k.lebedyantsev
 * Date: 7/6/2018
 * Time: 1:14 PM
 */
public class FinishedTripController extends AbstractController {
    private static final String TAG = FinishedTripController.class.getSimpleName();

    private TripInProcess tripInProcess;
    private TripService tripService;

    public FinishedTripController(TripInProcess tripInProcess, TripService tripService) {
        this.tripInProcess = tripInProcess;
        this.tripService = tripService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {
        String id = httpWrap.getParameter("id");
        if (PLUGIN_DEBUG_MODE) {
            LogUtil.info(TAG, "FinishedTripController id: " + id);
        }
        return tripInProcess.get(httpWrap, user, id, COMPLETED);
    }

    @Override
    public HttpWrap processPut(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    @Override
    public HttpWrap processDelete(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    protected String getReadPermission() {
        return "life.trip.read";
    }

    protected String getWritePermission() {
        return "life.trip.write";
    }
}
