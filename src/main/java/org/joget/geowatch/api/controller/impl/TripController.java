package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.process.in.TripInProcess;
import org.joget.geowatch.db.dto.type.TripLifeStateType;
import org.joget.geowatch.db.service.TripService;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.joget.geowatch.db.dto.type.TripLifeStateType.LIVE;

/**
 * Created by k.lebedyantsev
 * Date: 1/11/2018
 * Time: 2:47 PM
 */
public class TripController extends AbstractController {
    private static final String TAG = TripController.class.getSimpleName();

    private TripInProcess tripInProcess;
    private TripService tripService;

    public TripController(TripInProcess tripInProcess, TripService tripService) {
        this.tripInProcess = tripInProcess;
        this.tripService = tripService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {
        String id = httpWrap.getParameter("id");
        return tripInProcess.post(httpWrap, user, id);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {
        String id = httpWrap.getParameter("id");
        return tripInProcess.get(httpWrap, user, id, LIVE);
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
