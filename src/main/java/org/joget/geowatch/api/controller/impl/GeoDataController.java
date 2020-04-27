package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.db.service.GeoDataService;
import org.joget.geowatch.util.DateUtil;

import java.util.Date;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 2:33 PM
 */
public class GeoDataController extends AbstractController {
    private static final String TAG = GeofenceController.class.getSimpleName();

    private GeoDataService geoDataService;

    public GeoDataController(GeoDataService geoDataService) {
        this.geoDataService = geoDataService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {

        String id = httpWrap.getParameter("id");
        String timestamp = httpWrap.getParameter("timestamp");

        if (isBlank(id)) httpWrap.error(SC_BAD_REQUEST, null);

        Date dateStart = DateUtil.getFeApiDate(timestamp);
        httpWrap.result(SC_OK, geoDataService.listLiveTripGeoData(
                id, dateStart != null ? dateStart : new Date(1L), user));

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
