package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.GhtVehicle;
import org.joget.geowatch.db.service.GhtVehicleService;
import org.joget.geowatch.ghtrack.service.GhTourService;

import java.util.Collection;
import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 3:14 PM
 */
public class GhtVehicleServiceImpl implements GhtVehicleService {
    private static final String TAG = GhtVehicleServiceImpl.class.getName();

    private SessionFactory sessionFactory;
    private Dao<GhtVehicle> ghtVehicleDao;
    private GhTourService ghTourService;

    public GhtVehicleServiceImpl(
            SessionFactory sessionFactory, Dao<GhtVehicle> ghtVehicleDao, GhTourService ghTourService) {
        this.sessionFactory = sessionFactory;
        this.ghtVehicleDao = ghtVehicleDao;
        this.ghTourService = ghTourService;
    }

    @Override
    public GhtVehicle get(String ghtVehicleId) throws Exception {
        GhtVehicle res = null;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            res = ghtVehicleDao.findById(ghtVehicleId);

            transaction.commit();
            transaction = null;
            return res;

        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public Collection<GhtVehicle> list() throws Exception {
        Collection<GhtVehicle> ghtGhtVehicles = null;
        try {
            ghtGhtVehicles = ghTourService.listVehicle();
            create(ghtGhtVehicles);
        } catch (Exception e) {
            LogUtil.error(TAG, e, "Error on getting list vehicleList");
        }
        return ghtGhtVehicles;
    }

    @Override
    public void create(Collection<GhtVehicle> ghtVehicleCollection) throws Exception {
        if (ghtVehicleCollection == null || ghtVehicleCollection.size() == 0) return;

        for (GhtVehicle ghtVehicle : ghtVehicleCollection) {
            Date date = new Date();
            ghtVehicle.setDateModified(date);
            ghtVehicle.setDateCreated(date);
            try {
                saveOrUpdate(ghtVehicle);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "Error. Cannot save or update Ght Vehicle: " + ghtVehicle);
            }
        }
    }

        @Override
        public void saveOrUpdate (GhtVehicle ghtVehicle) throws Exception {
            Transaction transaction = null;
            Session session = null;

            try {
                session = sessionFactory.getCurrentSession();
                transaction = session.beginTransaction();

                ghtVehicleDao.saveOrUpdate(ghtVehicle);

                transaction.commit();
                transaction = null;

            } finally {
                if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
                if (session != null && session.isOpen()) session.close();
            }
        }

    }
