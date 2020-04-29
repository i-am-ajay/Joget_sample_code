package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.dto.out.resp.NotifyOutResp;
import org.joget.geowatch.api.process.in.NotifyInProcess;

import java.util.Date;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.joget.geowatch.util.DateUtil.getFeApiDate;

/**
 * Created by k.lebedyantsev
 * Date: 3/26/2018
 * Time: 12:28 PM
 */
public class NotifyController extends AbstractController {
    private static final String TAG = NotifyController.class.getSimpleName();

    private NotifyInProcess notifyInProcess;

    public NotifyController(NotifyInProcess notifyInProcess) {
        this.notifyInProcess = notifyInProcess;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {
        NotifyOutResp notifyOutResp = httpWrap.getBody(NotifyOutResp.class);
        System.out.println("Snooze duration :");
        System.out.println(notifyOutResp.getSnoozeduration());
        return notifyInProcess.post(httpWrap, user, notifyOutResp);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {

        String tripId = httpWrap.getParameter("idTrip");
        String notifyId = httpWrap.getParameter("notifyId");
        Date date = getFeApiDate(httpWrap.getParameter("timestamp"));
        Integer limit = isNotBlank(httpWrap.getParameter("limit"))
                ? Integer.valueOf(httpWrap.getParameter("limit")) : null;

        return notifyInProcess.get(httpWrap, user, tripId, notifyId, date, limit);
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
        return "notify.read";
    }

    protected String getWritePermission() {
        return "notify.write";
    }
}
