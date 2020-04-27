package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.out.resp.NotifyOutResp;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Event;
import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.db.service.NotifyService;
import org.joget.geowatch.type.EventType;
import org.joget.geowatch.type.NotifyType;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joget.geowatch.db.dao.impl.AbstractDao.OrderType.DESC;
import static org.joget.geowatch.db.dto.type.NotifyResolveStatusType.CLOSED;
import static org.joget.geowatch.util.DateUtil.getUiShortStrDate;

/**
 * Created by k.lebedyantsev
 * Date: 3/26/2018
 * Time: 12:37 PM
 */
public class NotifyServiceImpl implements NotifyService {
    private static final String TAG = NotifyServiceImpl.class.getSimpleName();

    private SessionFactory sessionFactory;
    private Dao<Notify> notifyDao;
    private Dao<Event> eventDao;

    public NotifyServiceImpl(SessionFactory sessionFactory, Dao<Notify> notifyDao, Dao<Event> eventDao) {
        this.sessionFactory = sessionFactory;
        this.notifyDao = notifyDao;
        this.eventDao = eventDao;
    }

    @Override
    public Map<EventType, Notify> getLast(String tripId, String ghtVehicleId, EventType[] eventTypeArr) throws Exception {
        Map<EventType, Notify> map = new HashMap<>();
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            for (EventType eventType : eventTypeArr) {
                Notify notify = notifyDao.findSingle(
                        "SELECT e FROM " + Notify.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId " +
                                "AND  e.ghtVehicleId = :ghtVehicleId " +
                                "AND e.eventType = :eventType ",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.date", DESC)},
                        new AbstractDao.StrParam("tripId", tripId),
                        new AbstractDao.StrParam("ghtVehicleId", ghtVehicleId),
                        new AbstractDao.StrParam("eventType", eventType.name()));
                if (notify != null) map.put(eventType, notify);
            }

            transaction.commit();
            transaction = null;
            return map;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public void save(Collection<Notify> notifyList) {
        if (notifyList == null || notifyList.size() == 0) return;

        for (Notify notify : notifyList) {
            try {
                String res = save(notify);
                if (isEmpty(res))
                    throw new RuntimeException("I can't save notify: " + notify);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR.");
            }
        }
    }

    @Override
    public String save(Notify notify) throws Exception {
        String res = null;
        if (notify == null) return res;

        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            res = notifyDao.save(notify);

            transaction.commit();
            transaction = null;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) {
                LogUtil.warn(TAG,"TRANSACTION ROLLBACK save(Notify notify). notifyId:" + notify.getId());
                transaction.rollback();
            }
            if (session != null && session.isOpen()) session.close();
        }
        return res;
    }

    @Override
    public List<Notify> list(String tripId, Date date, Integer limit) throws Exception {
        List<Notify> res;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            if (date == null) date = new Date(1L);

            res = notifyDao.find(
                    "SELECT e FROM " + Notify.class.getSimpleName() + " e " +
                            "WHERE e.date > :date " +
                            "AND e.tripId = :tripId " +
                            "AND e.notifyType IN ('" + NotifyType.ALERT.name() + "', '" + NotifyType.NOTICE.name() + "') ",
                    new AbstractDao.Order[]{new AbstractDao.Order("e.date", DESC)},
                    new AbstractDao.Page(null, limit),
                    new AbstractDao.DateParam("date", date),
                    new AbstractDao.StrParam("tripId", tripId)
            );

            if (res != null) for (Notify notify : res) {
                notify.setEvent1(eventDao.findById(notify.getEvent1Id()));
                notify.setEvent2(eventDao.findById(notify.getEvent2Id()));
            }

            transaction.commit();
            transaction = null;
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public Notify getWithEvent(String notifyId) throws Exception {
        Notify res = null;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            res = notifyDao.findById(notifyId);
            res.setEvent1(eventDao.findById(res.getEvent1Id()));
            res.setEvent2(eventDao.findById(res.getEvent2Id()));

            transaction.commit();
            transaction = null;
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public void update(Collection<Notify> notifyList) {
        if (notifyList == null || notifyList.size() == 0) return;

        for (Notify notify : notifyList) {
            try {
                update(notify);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "Error. Save event.");
            }
        }
    }

    @Override
    public void update(Notify notify) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            notifyDao.update(notify);

            transaction.commit();
            transaction = null;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) {
                LogUtil.warn(TAG,"TRANSACTION ROLLBACK update(Notify notify)). notifyId:" + notify.getId());
                transaction.rollback();
            }
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public NotifyOutResp update(User user, NotifyOutResp notifyOutResp) throws Exception {
        if (isEmpty(notifyOutResp.getId())) return null;

        NotifyOutResp res;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Notify notify = notifyDao.findById(notifyOutResp.getId());

            notify.setNote(notifyOutResp.getNote());
            notify.setSnoozeduration(notifyOutResp.getSnoozeduration());

            Date date = new Date();
            notify.setDateModified(date);
            notify.setHandleDate(date);

            notify.setStatus(CLOSED);
            notify.setHandler(user.getId());

            notifyDao.update(notify);

            transaction.commit();
            transaction = null;

            res = new NotifyOutResp();
            res.setId(notify.getId());
            res.setHandler(notify.getHandler());
            res.setStatus(notify.getStatus());
            res.setHandleDate(getUiShortStrDate(notify.getHandleDate(), user));
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) {
                LogUtil.warn(TAG,"TRANSACTION ROLLBACK update(User user, NotifyOutResp notifyOutResp). notifyId:" + notifyOutResp.getId());
                transaction.rollback();
            }
            if (session != null && session.isOpen()) session.close();
        }
    }
}
