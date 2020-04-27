package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.geowatch.api.dto.out.resp.VehicleOutResp;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleInnerEntity;
import org.joget.geowatch.db.dto.type.VehicleType;
import org.joget.geowatch.db.service.VehicleService;

import static org.joget.geowatch.db.dao.impl.AbstractDao.OrderType.DESC;

/**
 * Created by k.lebedyantsev
 * Date: 2/13/2018
 * Time: 5:35 PM
 */
public class VehicleServiceImpl implements VehicleService {
    private static final String TAG = VehicleServiceImpl.class.getSimpleName();

    private SessionFactory sessionFactory;
    private Dao<Trip> tripDao;
    private Dao<Log> logDao;

    public VehicleServiceImpl(
            SessionFactory sessionFactory, Dao<Trip> tripDao, Dao<Log> logDao) {
        this.sessionFactory = sessionFactory;
        this.tripDao = tripDao;
        this.logDao = logDao;
    }

    @Override
    public VehicleOutResp getTripVehicle(String tripId, VehicleType vehicleType) throws Exception {
        VehicleOutResp res = null;

        Trip trip = null;
        VehicleInnerEntity vehicle = null;
        GhtVehicleInnerEntity ghtVehicle = null;
        Log log = null;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            trip = tripDao.findById(tripId);
            if (trip != null) {
                if (VehicleType.HAULIER == vehicleType) {
                    vehicle = trip.getHaulierVehicle();
                    ghtVehicle = trip.getHaulierGhtVehicle();
                } else if (VehicleType.TRAILER == vehicleType) {
                    vehicle = trip.getHaulierTrailerVehicle();
                    ghtVehicle = trip.getHaulierTrailerGhtVehicle();
                } else if (VehicleType.RMO == vehicleType) {
                    vehicle = trip.getRmoVehicle();
                    ghtVehicle = trip.getRmoGhtVehicle();
                }

                log = logDao.findSingle(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.ghtVehicleId = :ghtVehicleId " +
                                "AND e.lat IS NOT NULL " +
                                "AND e.lng IS NOT NULL ",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.date", DESC)},
                        new AbstractDao.StrParam("ghtVehicleId", ghtVehicle.getId()));
            }
            transaction.commit();
            transaction = null;

            if (trip != null)
                res = VehicleOutResp.update(new VehicleOutResp(), vehicleType, vehicle, ghtVehicle, trip.getHaulierLastPosition(), log);

            return res;

        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }
}
