package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.dto.out.resp.TripTestOutResp;
import org.joget.geowatch.db.service.TripTestService;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by k.lebedyantsev
 * Date: 1/11/2018
 * Time: 2:47 PM
 */
public class TripTestController extends AbstractController {
    private static final String TAG = TripTestController.class.getSimpleName();

    private TripTestService tripTestService;

    public TripTestController(TripTestService tripTestService) {
        this.tripTestService = tripTestService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {

        String id = httpWrap.getParameter("id");
        if (isBlank(id)) return httpWrap.error(SC_BAD_REQUEST, null);

        TripTestOutResp res = tripTestService.sensors(id);
        if (res != null) httpWrap.result(SC_OK, res);

        return httpWrap;
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
