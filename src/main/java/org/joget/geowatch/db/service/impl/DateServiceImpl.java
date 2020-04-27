package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.service.DateService;

import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 7/30/2018
 * Time: 11:55 AM
 */
public class DateServiceImpl implements DateService {
    private SessionFactory sessionFactory;
    private Dao dateDao;

    public DateServiceImpl(SessionFactory sessionFactory, Dao dateDao) {
        this.sessionFactory = sessionFactory;
        this.dateDao = dateDao;
    }

    @Override
    public Date getDbDate() {
        Date date = null;
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            session.setDefaultReadOnly(true);

            date = (Date) dateDao.findNativeSqlSingle("SELECT SYSDATE() FROM DUAL", new Date(), null);
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
        return date;
    }
}
