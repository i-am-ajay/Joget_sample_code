package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.LogJson;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.service.LogService;
import org.joget.geowatch.processing.dto.VehicleProcessData;
import org.joget.geowatch.util.ApiUtil;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.joget.geowatch.db.dao.impl.AbstractDao.OrderType.ASC;
import static org.joget.geowatch.db.dao.impl.AbstractDao.OrderType.DESC;
import static org.joget.geowatch.db.dto.type.VehicleType.HAULIER;
import static org.joget.geowatch.db.dto.type.VehicleType.RMO;
import static org.joget.geowatch.db.dto.type.VehicleType.TRAILER;

/**
 * Created by k.lebedyantsev
 * Date: 2/13/2018
 * Time: 12:38 PM
 */
public class LogServiceImpl implements LogService {
    private static final String TAG = LogServiceImpl.class.getSimpleName();

    private SessionFactory sessionFactory;
    private Dao<Log> logDao;

    public LogServiceImpl(Dao<Log> logDao, SessionFactory sessionFactory) {
        this.logDao = logDao;
        this.sessionFactory = sessionFactory;
    }

    public void save(Collection<Log> logCollection) {
    	LogUtil.info(this.getClass().getName(), "LOGS for saving");
    //	LogUtil.info(this.getClass().getName(), logCollection.toString());
        if (logCollection == null || logCollection.size() == 0) return;

        
        for (Log log : logCollection) {
            try {
                String res = save(log);
                if (isEmpty(res))
                    throw new RuntimeException("I can't save log: " + log);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR.");
            }
        }
    }

    public String save(Log log) throws Exception {
        String res = null;
        if (log == null) return res;

        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            
            log.setId(log.getTripId()+"_"+log.getDate()+"_"+log.getVehicleId()+"_"+log.getGhtVehicleId());
            try {
            Log exist=logDao.findById(log.getId());
            if(exist!=null)
            {
            	LogUtil.debug(TAG, "Already exist! "+log.getId());
            	return "Already exist! "+log.getId();
            }
            }
            catch(Exception e) {
            	e.printStackTrace();
            }
            res = logDao.save(log);

            transaction.commit();
            transaction = null;
            return res;
        }
        catch(Exception e) {
        	LogUtil.debug(TAG,"Error in saving log "+log.getId());
        	e.printStackTrace();
        	throw e;
        }
        finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    public VehicleProcessData updateLastTimeLogReqAndLog(VehicleProcessData vehicleProcessData) throws Exception {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Log log;
            if (vehicleProcessData.getTrip().getHaulierGhtVehicle() != null) {
                log = logDao.findSingle(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId " +
                                "AND e.ghtVehicleId = :ghtVehicleId",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.date", DESC)},
                        new AbstractDao.StrParam("tripId", vehicleProcessData.getTrip().getId()),
                        new AbstractDao.StrParam("ghtVehicleId", vehicleProcessData.getTrip().getHaulierGhtVehicle().getId()));
                vehicleProcessData.addLastLog(HAULIER, log);
                
                log = logDao.findSingle(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId " +
                                "AND e.ghtVehicleId = :ghtVehicleId",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.serverDate", DESC)},
                        new AbstractDao.StrParam("tripId", vehicleProcessData.getTrip().getId()),
                        new AbstractDao.StrParam("ghtVehicleId", vehicleProcessData.getTrip().getHaulierGhtVehicle().getId()));
                if (log != null) {
                	vehicleProcessData.addLastLogReqDate(HAULIER, log.getServerDate());
                	
                }
            }

            if (vehicleProcessData.getTrip().getHaulierTrailerGhtVehicle() != null) {
                log = logDao.findSingle(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId " +
                                "AND e.ghtVehicleId = :ghtVehicleId",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.date", DESC)},
                        new AbstractDao.StrParam("tripId", vehicleProcessData.getTrip().getId()),
                        new AbstractDao.StrParam("ghtVehicleId", vehicleProcessData.getTrip().getHaulierTrailerGhtVehicle().getId()));
                vehicleProcessData.addLastLog(TRAILER, log);
                 log = logDao.findSingle(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId " +
                                "AND e.ghtVehicleId = :ghtVehicleId",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.serverDate", DESC)},
                        new AbstractDao.StrParam("tripId", vehicleProcessData.getTrip().getId()),
                        new AbstractDao.StrParam("ghtVehicleId", vehicleProcessData.getTrip().getHaulierTrailerGhtVehicle().getId()));
                if (log != null) {
                	vehicleProcessData.addLastLogReqDate(TRAILER, log.getServerDate());
                	
                }
            }

            if (vehicleProcessData.getTrip().getRmoGhtVehicle() != null) {
                log = logDao.findSingle(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId " +
                                "AND e.ghtVehicleId = :ghtVehicleId",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.date", DESC)},
                        new AbstractDao.StrParam("tripId", vehicleProcessData.getTrip().getId()),
                        new AbstractDao.StrParam("ghtVehicleId", vehicleProcessData.getTrip().getRmoGhtVehicle().getId()));
                vehicleProcessData.addLastLog(RMO, log);
               
                log = logDao.findSingle(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId " +
                                "AND e.ghtVehicleId = :ghtVehicleId",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.serverDate", DESC)},
                        new AbstractDao.StrParam("tripId", vehicleProcessData.getTrip().getId()),
                        new AbstractDao.StrParam("ghtVehicleId", vehicleProcessData.getTrip().getRmoGhtVehicle().getId()));
                if (log != null) {
                	vehicleProcessData.addLastLogReqDate(RMO, log.getServerDate());
                	
                }
            }

            transaction.commit();
            transaction = null;
           
            
            return vehicleProcessData;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public Date getLastLogDate(String tripId) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Log res = logDao.findSingle(
                    "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                            "WHERE e.tripId = :tripId",
                    new AbstractDao.Order[]{new AbstractDao.Order("e.date", DESC)},
                    new AbstractDao.StrParam("tripId", tripId));

            transaction.commit();
            transaction = null;
            return res != null ? res.getServerDate() : null;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<LogJson> list(String tripId, Date date, User user, Integer offset, Integer limit) throws Exception {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            List<Log> logs;
            if (date != null) {
                logs = logDao.find(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.date > :date AND e.tripId = :tripId group by e.tripId,e.date,e.ghtVehicle",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.date", ASC)},
                        new AbstractDao.Page(offset, limit),
                        new AbstractDao.DateParam("date", date),
                        new AbstractDao.StrParam("tripId", tripId));

            } else {
                logs = logDao.find(
                        "SELECT e FROM " + Log.class.getSimpleName() + " e " +
                                "WHERE e.tripId = :tripId",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.date", ASC)},
                        new AbstractDao.Page(offset, limit),
                        new AbstractDao.StrParam("tripId", tripId));
            }
            transaction.commit();
            transaction = null;
            return ApiUtil.remapListLogDtoToListLogJson(logs, user);
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public LogJson get(String idLog, User currentUser) throws Exception {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Log log = logDao.findById(idLog);
            transaction.commit();
            transaction = null;
            return ApiUtil.remapLogDtoToLogJson(log, currentUser);
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }
}
