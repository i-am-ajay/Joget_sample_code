package org.joget.geowatch.ghtrack.service.impl;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.service.LogService;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.Vehicle;
import org.joget.geowatch.ght.net.dto.in.resp.log.LogGhtVehicleInResp;
import org.joget.geowatch.ght.net.dto.in.resp.error.GhtVehicleError;
import org.joget.geowatch.ght.net.dto.in.resp.GhtVehicleInResp;
import org.joget.geowatch.ghtrack.service.GhtVehicleService;
import org.joget.geowatch.util.ApiUtil;
import org.joget.geowatch.util.UrlUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Calendar.SECOND;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.joget.geowatch.app.AppProperties.GHT_BASE_URL;
import static org.joget.geowatch.app.AppProperties.GHT_VEHICLE_API_KEY;
import static org.joget.geowatch.util.DateUtil.getGhtStrDate;
import static org.joget.geowatch.util.UrlUtil.Method.GET;
import static org.joget.geowatch.util.UrlUtil.getObj;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 4:36 PM
 */
public class GhtVehicleServiceImpl extends AbstractGhTrackService implements GhtVehicleService {
    private static final String TAG = GhtVehicleServiceImpl.class.getSimpleName();

    private static final String GET_VEHICLE_URL = GHT_BASE_URL + "/api/v2/vehicle/%s?from_ts=%s";

    private Dao<Trip> tripDao;
    private LogService logService;
    private SessionFactory sessionFactory;

    public GhtVehicleServiceImpl(LogService logService, Dao<Trip> tripDao, SessionFactory sessionFactory) {
        this.logService = logService;
        this.tripDao = tripDao;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Map<String, List<GhtVehicleInResp>> pullLiveTripGhtVehicleLog(Map<String, List<Vehicle>> lifeTripVehiclesMap) throws Exception {
        Map<String, List<GhtVehicleInResp>> vehicleRootObjectMap = new HashMap<>();
        for (Map.Entry<String, List<Vehicle>> entry : lifeTripVehiclesMap.entrySet()) {
            String lifeTripId = entry.getKey();
            Date lastLogDate = getLastLogTripDate(lifeTripId);

            List<GhtVehicleInResp> ghtVehiclesInfo = new ArrayList<>();
            for (Vehicle vehicle : entry.getValue()) {
                String ghtVehicleId = vehicle.getGhtVehicleId();
                try {
                    UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError> vehicleLog =
                            pullGhtVehicleLogById(ghtVehicleId, lastLogDate);
                    ghtVehiclesInfo.add(new GhtVehicleInResp(lifeTripId, vehicle.getId(), ghtVehicleId, vehicleLog.getResultObj(), vehicleLog.getErrorObj()));
                } catch (Exception e) {
                    LogUtil.error(TAG, e, "Error pull GhtVehicleLog. ghtVehicleId: " + ghtVehicleId + ", lastLogDate: " + lastLogDate);
                }
            }
            vehicleRootObjectMap.put(lifeTripId, ghtVehiclesInfo);
        }
        return vehicleRootObjectMap;
    }

    public UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError> pullGhtVehicleLogById(String ghtVehicleId, Date lastLogDate) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTime(lastLogDate);
        cal.add(SECOND, 1);

        UrlUtil.RespResult<LogGhtVehicleInResp, GhtVehicleError> respRes =
                getObj(LogGhtVehicleInResp.class, GhtVehicleError.class,
                        GET, GET_VEHICLE_URL, new String[]{ghtVehicleId, getGhtStrDate(cal.getTime())}, null,
                        new UrlUtil.Header[]{
                                new UrlUtil.Header("Content-Type", "application/json"),
                                new UrlUtil.Header("X-Api-Key", GHT_VEHICLE_API_KEY)
                        });
        return respRes;
    }

    private Date getLastLogTripDate(String lifeTripId) throws Exception {
        Session session = null;
        Transaction transaction = null;

        try {
            Date date = logService.getLastLogDate(lifeTripId);
            if (date == null) {
                session = sessionFactory.getCurrentSession();
                transaction = session.beginTransaction();

                Trip trip = tripDao.findSingle(
                        "SELECT e FROM " + Trip.class.getSimpleName() + " e " +
                                "WHERE e.id = :id",
                        new AbstractDao.StrParam("id", lifeTripId));

                transaction.commit();
                transaction = null;

                if (trip != null) {
                    if (isNotBlank(trip.getStartDateTime())) {
                        String tripDateString = trip.getStartDateTime();
                        SimpleDateFormat format = ApiUtil.getFormatter(ApiUtil.getDbRouteDateFormat());
                        try {
                            date = format.parse(tripDateString);
                        } catch (ParseException e) {
                            LogUtil.error(TAG, e, "Error on parsing date.");
                        }
                    }
                }
            } else {
                date = DateUtils.addSeconds(date, 1);
            }
            LogUtil.info(TAG, "getLastLogTripDate. lifeTripId: " + lifeTripId + ", date: " + date);
            return date;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }
}
