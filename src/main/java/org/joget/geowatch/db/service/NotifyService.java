package org.joget.geowatch.db.service;

import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.out.resp.NotifyOutResp;
import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.type.EventType;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by k.lebedyantsev
 * Date: 3/26/2018
 * Time: 12:37 PM
 */
public interface NotifyService {
    Map<EventType, Notify> getLast(String tripId, String ghtVehicle, EventType[] eventTypeArr) throws Exception;

    void save(Collection<Notify> notifyList);

    String save(Notify notify) throws Exception;

    List<Notify> list(String tripId, Date date, Integer limit) throws Exception;

    Notify getWithEvent(String notifyId) throws Exception;

    void update(Collection<Notify> notifyList);

    void update(Notify notify) throws Exception;

    NotifyOutResp update(User user, NotifyOutResp notifyJson) throws Exception;
}

