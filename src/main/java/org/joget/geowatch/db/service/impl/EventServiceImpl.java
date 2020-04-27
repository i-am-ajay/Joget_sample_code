package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Event;
import org.joget.geowatch.db.service.EventService;
import org.joget.geowatch.type.EventType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EventServiceImpl implements EventService {
    private static final String TAG = EventServiceImpl.class.getSimpleName();

    protected SessionFactory sessionFactory;
    protected Dao<Event> eventDao;

    public EventServiceImpl(SessionFactory sessionFactory, Dao<Event> eventDao) {
        this.sessionFactory = sessionFactory;
        this.eventDao = eventDao;
    }

    public Map<EventType, Event> getLast(String tripId, String ghtVehicleId, EventType[] eventTypeArr) throws Exception {
        Map<EventType, Event> map = new HashMap<>();
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            for (EventType eventType : eventTypeArr) {
                Event event = eventDao.findSingle(
                        "SELECT e FROM " + Event.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId " +
                                "AND  e.ghtVehicleId = :ghtVehicleId " +
                                "AND e.eventType = :eventType ",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.date", AbstractDao.OrderType.DESC)},
                        new AbstractDao.StrParam("tripId", tripId),
                        new AbstractDao.StrParam("ghtVehicleId", ghtVehicleId),
                        new AbstractDao.StrParam("eventType", eventType.name()));
                if (event != null) map.put(eventType, event);
            }

            transaction.commit();
            transaction = null;
            return map;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    public void save(Collection<Event> eventList) {
        if (eventList == null || eventList.size() == 0) return;

        for (Event event : eventList) {
            try {
                String res = save(event);
                if (isEmpty(res))
                    throw new RuntimeException("I can't save event: " + event);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR.");
            }
        }
    }

    public String save(Event event) throws Exception {
        String res = null;
        if (event == null) return res;

        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            res = eventDao.save(event);

            transaction.commit();
            transaction = null;
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }
}
