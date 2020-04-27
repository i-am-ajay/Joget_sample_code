package org.joget.geowatch.api.process.in;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.dto.out.resp.NotifyOutResp;
import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.db.service.NotifyService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class NotifyInProcess {

    private NotifyService notifyService;

    public NotifyInProcess(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    public HttpWrap post(HttpWrap httpWrap, User user, NotifyOutResp notifyOutResp) throws Exception {

        if (notifyOutResp != null) {
            notifyService.update(user, notifyOutResp);
            Notify notify = notifyService.getWithEvent(notifyOutResp.getId());
            NotifyOutResp res = NotifyOutResp.update(new NotifyOutResp(), notify, user);
            if (res != null) httpWrap.result(SC_OK, res);
        } else httpWrap.result(SC_BAD_REQUEST, null);

        return httpWrap;
    }

    public HttpWrap get(HttpWrap httpWrap, User user, String tripId, String notifyId, Date date, Integer limit) throws Exception {

        if (isNotBlank(notifyId)) {
            Notify notify = notifyService.getWithEvent(notifyId);
            NotifyOutResp notifyOutResp = NotifyOutResp.update(new NotifyOutResp(), notify, user);
            httpWrap.result(SC_OK, notifyOutResp);
        } else if (isNotBlank(tripId)) {
//            httpWrap.result(SC_OK, Collections.EMPTY_LIST);  //ToDo: Debug For Valera

            List<Notify> notifies = notifyService.list(tripId, date, limit);
            NotifyOutResp[] notifyOutResps = NotifyOutResp.update(
                    new NotifyOutResp[notifies.size()], notifies.toArray(new Notify[]{}), user);
            httpWrap.result(SC_OK, notifyOutResps);
        } else httpWrap.result(SC_BAD_REQUEST, Collections.EMPTY_LIST);
        return httpWrap;
    }
}
