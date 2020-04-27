package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.geowatch.api.dto.out.resp.TripTestOutResp;
import org.joget.geowatch.db.service.TripTestService;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.GhtVehicle;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.Vehicle;
import org.joget.geowatch.db.dto.Log;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joget.geowatch.util.DateUtil.getJogetDate;

public class TripTestServiceImpl implements TripTestService {
    private SessionFactory sessionFactory;
    private Dao<Trip> tripDao;
    private Dao<Vehicle> vehicleDao;
    private Dao<GhtVehicle> ghtVehicleDao;
    private Dao<Log> ghtLogDao;

    public TripTestServiceImpl(SessionFactory sessionFactory, Dao<Trip> tripDao, Dao<Vehicle> vehicleDao, Dao<GhtVehicle> ghtVehicleDao, Dao<Log> ghtLogDao) {
        this.sessionFactory = sessionFactory;
        this.tripDao = tripDao;
        this.vehicleDao = vehicleDao;
        this.ghtVehicleDao = ghtVehicleDao;
        this.ghtLogDao = ghtLogDao;
    }

    private static final String sensorStrQuery1 =
            "SELECT e FROM " + Log.class.getSimpleName() + " e " +
            "WHERE e.ghtVehicleId = :ghtVehicleId " +
            "AND e.date BETWEEN :dateFrom AND :dateTill ";

    @Override
    public TripTestOutResp sensors(String tripId) throws Exception {
        TripTestOutResp res = new TripTestOutResp();

        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Trip trip = tripDao.findById(tripId);
            Vehicle haulerVehicle = vehicleDao.findById(trip.getHaulierVehicleId());
            GhtVehicle haulerGhtVehicle = ghtVehicleDao.findById(haulerVehicle.getGhtVehicleId());

            List<Log> haulerLog = ghtLogDao.find(sensorStrQuery1,
                    new AbstractDao.StrParam("ghtVehicleId", haulerVehicle.getGhtVehicleId()),
                    new AbstractDao.DateParam("dateFrom", getJogetDate(trip.getStartDateTime())),
                    new AbstractDao.DateParam("dateTill", getJogetDate(trip.getFinishDateTime()))
            );

            res.setTripId(tripId);
            res.setVehicles(new ArrayList<TripTestOutResp.Vehicle>());
            res.getVehicles().add(new TripTestOutResp.Vehicle());
            res.getVehicles().get(0).update(haulerVehicle);
            res.getVehicles().get(0).update(haulerGhtVehicle);
            process(res.getVehicles().get(0), haulerLog);

            Vehicle rmoVehicle = vehicleDao.findById(trip.getRmoVehicleId());
            if (rmoVehicle != null) {
                GhtVehicle rmoGhtVehicle = ghtVehicleDao.findById(rmoVehicle.getGhtVehicleId());
                List<Log> rmoLog = ghtLogDao.find(sensorStrQuery1,
                        new AbstractDao.StrParam("ghtVehicleId", rmoVehicle.getGhtVehicleId()),
                        new AbstractDao.DateParam("dateFrom", getJogetDate(trip.getStartDateTime())),
                        new AbstractDao.DateParam("dateTill", getJogetDate(trip.getFinishDateTime()))
                );
                res.getVehicles().add(new TripTestOutResp.Vehicle());
                res.getVehicles().get(0).update(rmoVehicle);
                res.getVehicles().get(0).update(rmoGhtVehicle);
                process(res.getVehicles().get(0), rmoLog);
            }

            transaction.commit();
            transaction = null;
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    private TripTestOutResp.Vehicle process(TripTestOutResp.Vehicle ttVehicle, List<Log> logs) {
        String[] sensorValues1 = new String[2];
        String[] sensorValues2 = new String[2];
        String[] sensorValues3 = new String[2];
        String[] sensorValues4 = new String[2];
        String[] sensorValues5 = new String[2];
        String[] sensorValues6 = new String[2];
        String[] sensorValues7 = new String[2];
        String[] sensorValues8 = new String[2];

        if (logs != null) for (Log log : logs) {
            process(sensorValues1, log.getTempSensor1());
            process(sensorValues2, log.getTempSensor2());
            process(sensorValues3, log.getTempSensor3());
            process(sensorValues4, log.getTempSensor4());
            process(sensorValues5, log.getTempSensor5());
            process(sensorValues6, log.getTempSensor8());
            process(sensorValues7, log.getTempSensor7());
            process(sensorValues8, log.getTempSensor7());
        }

        ttVehicle.setSensor1(getSensor("name1", sensorValues1));
        ttVehicle.setSensor2(getSensor("name2", sensorValues2));
        ttVehicle.setSensor3(getSensor("name3", sensorValues3));
        ttVehicle.setSensor4(getSensor("name4", sensorValues4));
        ttVehicle.setSensor5(getSensor("name5", sensorValues5));
        ttVehicle.setSensor6(getSensor("name6", sensorValues6));
        ttVehicle.setSensor7(getSensor("name7", sensorValues7));
        ttVehicle.setSensor8(getSensor("name8", sensorValues8));
        return ttVehicle;
    }

    private void process(String[] sensorValues, String sensorValue) {
        if (isBlank(sensorValue)) return;
        if (sensorValues == null) sensorValues = new String[]{};

        if (isBlank(sensorValues[0])) sensorValues[0] = sensorValue;
        else if (sensorValues[0].equals(sensorValue) && isBlank(sensorValues[1]))
            sensorValues[1] = sensorValue;
    }

    private TripTestOutResp.Sensor getSensor(String name, String[] sensorValues) {
        String value = String.format("%s / %s",
                (!isBlank(sensorValues[0])) ? sensorValues[0] : " ",
                (!isBlank(sensorValues[1])) ? sensorValues[1] : " ");
        return new TripTestOutResp.Sensor(name, value);
    }
}
