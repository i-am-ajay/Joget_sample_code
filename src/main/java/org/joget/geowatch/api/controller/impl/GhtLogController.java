package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.db.service.LogService;

import java.util.Date;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.joget.geowatch.util.DateUtil.getFeApiDate;

/**
 * Created by k.lebedyantsev
 * Date: 3/21/2018
 * Time: 12:21 PM
 */
public class GhtLogController extends AbstractController {
    private static final String TAG = GhtLogController.class.getSimpleName();

    private LogService logService;

    public GhtLogController(LogService logService) {
        this.logService = logService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {

        String idTrip = httpWrap.getParameter("idTrip");
        String idLog = httpWrap.getParameter("idLog");
        String limit = httpWrap.getParameter("limit");
        String timestamp = httpWrap.getParameter("timestamp");

        if (isNotBlank(idLog)) httpWrap.result(SC_OK, logService.get(idLog, user));
        else if (isNotBlank(idTrip) && isNotBlank(limit)) {
//            httpWrap.result(SC_OK, Collections.EMPTY_LIST);  //ToDo: Debug For Valera

            Date date = getFeApiDate(timestamp);
            httpWrap.result(SC_OK, logService.list(idTrip, date, user, 0, Integer.valueOf(limit)));
        } else httpWrap.error(SC_BAD_REQUEST, null);

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
        return null;
    }
}
