package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.Location;
import org.joget.geowatch.api.dto.LogJson;
import org.joget.geowatch.api.dto.VehicleJson;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.service.GeoDataService;
import org.joget.geowatch.util.ApiUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.joget.geowatch.db.dao.impl.AbstractDao.OrderType.ASC;
import static org.joget.geowatch.db.dto.type.VehicleType.HAULIER;
import static org.joget.geowatch.db.dto.type.VehicleType.RMO;
import static org.joget.geowatch.db.dto.type.VehicleType.TRAILER;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 2:32 PM
 */
public class GeoDataServiceImpl implements GeoDataService {
    private SessionFactory sessionFactory;
    private Dao<Log> ghtLogDao;
    private Dao<Trip> tripDao;

    public GeoDataServiceImpl(SessionFactory sessionFactory,
                              Dao<Log> ghtLogDao, Dao<Trip> tripDao) {
        this.sessionFactory = sessionFactory;
        this.ghtLogDao = ghtLogDao;
        this.tripDao = tripDao;
    }

    @Override
    public List<VehicleJson> listLiveTripGeoData(String tripId, Date dateStart, User currentUser) throws Exception {
        Session session = null;
        Transaction transaction = null;

        List<VehicleJson> vehicles = new ArrayList<>();
        List<Log> logs = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Trip trip = tripDao.findById(tripId);

            logs = ghtLogDao.find(
                    "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                            "WHERE e.tripId = :tripId " +
                            "AND e.date > :date " +
                            "AND e.lat IS NOT NULL " +
                            "AND e.lng IS NOT NULL   ",
                    new AbstractDao.Order[]{new AbstractDao.Order("e.date", ASC)},
                    new AbstractDao.StrParam("tripId", tripId),
                    new AbstractDao.DateParam("date", dateStart)
            );
            System.out.println("Start Date"+dateStart);
            System.out.println("Logs --"+logs);
            transaction.commit();
            transaction = null;

            if (trip != null && trip.getHaulierGhtVehicle() != null)
                vehicles.add(ApiUtil.remapGhtVehicleDtoToVehicleJson(
                        trip.getHaulierVehicleId(), trip.getHaulierGhtVehicle(), HAULIER));

            if (trip != null && trip.getHaulierTrailerGhtVehicle() != null)
                vehicles.add(ApiUtil.remapGhtVehicleDtoToVehicleJson(

                        trip.getHaulierTrailerVehicleId(), trip.getHaulierTrailerGhtVehicle(), TRAILER));

            if (trip != null && trip.getRmoGhtVehicle() != null)
                vehicles.add(ApiUtil.remapGhtVehicleDtoToVehicleJson(
                        trip.getRmoVehicleId(), trip.getRmoGhtVehicle(), RMO));


            System.out.println(vehicles);
            if (logs != null) {
                List<LogJson> logsJson = ApiUtil.remapListLogDtoToListLogJson(logs, currentUser);
                for (VehicleJson veh : vehicles) {
                    for (LogJson log : logsJson) {
                        if (log.getLocation() != null && log.getLocation().getLng() != null
                                && log.getLocation().getLat() != null && isNotBlank(log.getVehicleId()) && log.getVehicleId().equals(veh.getVehicleId())) {
                            if (veh.getAllLocations() == null) {
                                veh.setAllLocations(new ArrayList<Location>());
                            }
                            Location logLocation = log.getLocation();
                            logLocation.setLogId(log.getId());
                            veh.getAllLocations().add(logLocation);
                        }
                    }
                    if (veh.getAllLocations() != null && veh.getAllLocations().size() > 0) {
                        Location currentPosition = veh.getAllLocations().get(veh.getAllLocations().size() - 1);
                        veh.setCurrentPosition(currentPosition);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error(TripServiceImpl.class.getName(), e, "Error on getting trip");
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
        return vehicles;
    }

    @Override
    public List<VehicleJson> listTripGeoData(String tripId, User currentUser) throws Exception {
        Session session = null;
        Transaction transaction = null;

        List<VehicleJson> vehicles = new ArrayList<>();
        List<Log> logs = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Trip trip = tripDao.findById(tripId);

            logs = ghtLogDao.find(
                    "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                            "WHERE e.tripId = :tripId " +
                            "AND e.lat IS NOT NULL " +
                            "AND e.lng IS NOT NULL ",
                    new AbstractDao.Order[]{new AbstractDao.Order("e.date", ASC)},
                    new AbstractDao.StrParam("tripId", tripId)
            );

            transaction.commit();

            if (trip != null && trip.getHaulierGhtVehicle() != null)
                vehicles.add(ApiUtil.remapGhtVehicleDtoToVehicleJson(
                        trip.getHaulierVehicleId(), trip.getHaulierGhtVehicle(), HAULIER));

            if (trip != null && trip.getHaulierTrailerGhtVehicle() != null)
                vehicles.add(ApiUtil.remapGhtVehicleDtoToVehicleJson(

                        trip.getHaulierTrailerVehicleId(), trip.getHaulierTrailerGhtVehicle(), TRAILER));

            if (trip != null && trip.getRmoGhtVehicle() != null)
                vehicles.add(ApiUtil.remapGhtVehicleDtoToVehicleJson(
                        trip.getRmoVehicleId(), trip.getRmoGhtVehicle(), RMO));


            if (logs != null) {
                List<LogJson> logsJson = ApiUtil.remapListLogDtoToListLogJson(logs, currentUser);
                for (VehicleJson veh : vehicles) {
                    for (LogJson log : logsJson) {
                        if (log.getLocation() != null && log.getLocation().getLng() != null
                                && log.getLocation().getLat() != null && isNotBlank(log.getVehicleId()) && log.getVehicleId().equals(veh.getVehicleId())) {
                            if (veh.getAllLocations() == null) {
                                veh.setAllLocations(new ArrayList<Location>());
                            }
                            Location logLocation = log.getLocation();
                            logLocation.setLogId(log.getId());
                            veh.getAllLocations().add(logLocation);
                        }
                    }
                    if (veh.getAllLocations() != null && veh.getAllLocations().size() > 0) {
                        Location currentPosition = veh.getAllLocations().get(veh.getAllLocations().size() - 1);
                        veh.setCurrentPosition(currentPosition);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error(TripServiceImpl.class.getName(), e, "Error on getting trip");
            if (transaction != null) transaction.rollback();
        } finally {
            if (session != null && session.isOpen()) session.close();
        }
        return vehicles;
    }
}
