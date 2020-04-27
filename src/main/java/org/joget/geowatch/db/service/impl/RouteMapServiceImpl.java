package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Route;
import org.joget.geowatch.db.dto.RouteMap;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.inner.RouteMapInnerEntity;
import org.joget.geowatch.db.service.RouteMapService;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joget.geowatch.db.dao.impl.AbstractDao.OrderType.DESC;

/**
 * Created by k.lebedyantsev
 * Date: 1/3/2018
 * Time: 2:46 PM
 */
public class RouteMapServiceImpl implements RouteMapService {
    private SessionFactory sessionFactory;
    private Dao<Trip> tripDao;
    private Dao<Route> routeDao;
    private Dao<RouteMap> routeMapDao;

    public RouteMapServiceImpl(SessionFactory sessionFactory, Dao<Trip> tripDao, Dao<Route> routeDao, Dao<RouteMap> routeMapDao) {
        this.sessionFactory = sessionFactory;
        this.tripDao = tripDao;
        this.routeDao = routeDao;
        this.routeMapDao = routeMapDao;
    }

    public String save(RouteMap routeMap) throws Exception {
        String res;
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            res = routeMapDao.save(routeMap);

            transaction.commit();
            transaction = null;
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public boolean update(RouteMap routeMap) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            routeMapDao.update(routeMap);

            transaction.commit();
            transaction = null;
            return true;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public RouteMapInnerEntity getByTripId(String tripId) throws Exception {
        RouteMapInnerEntity res = null;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Trip trip = tripDao.findById(tripId);
            if(trip == null) res = null;
            else res = trip.getImageRouteMap();

            transaction.commit();
            transaction = null;
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public RouteMapInnerEntity getByRouteId(String routeId) throws Exception {
        RouteMapInnerEntity res = null;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Route route = routeDao.findById(routeId);
            if(route == null) res = null;
            else res = route.getImageRouteMap();

            transaction.commit();
            transaction = null;
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public RouteMap getByRouteMapId(String routeMapId) throws Exception {
        RouteMap res = null;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            res = routeMapDao.findById(routeMapId);

            transaction.commit();
            transaction = null;
            return res;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public List<RouteMap> list(String searchMask) throws Exception {
        List<RouteMap> res = null;

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            if (isBlank(searchMask)) {
                res = routeMapDao.find(
                        "SELECT e FROM " + RouteMap.class.getSimpleName() + " e ",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.name", DESC)});
            } else {
                res = routeMapDao.find(
                        "SELECT e FROM " + RouteMap.class.getSimpleName() + " e " +
                                "WHERE LOWER(e.name) LIKE LOWER(:name)",
                        new AbstractDao.Order[]{new AbstractDao.Order("e.name", DESC)},
                        new AbstractDao.StrParam("name", "%" + searchMask + "%"));
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
    public boolean delete(String routeMapId) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            routeMapDao.delete(new RouteMap(routeMapId));

            transaction.commit();
            transaction = null;
            return true;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

    @Override
    public Long checkRouteName(String checkName) throws Exception {
        Session session = null;
        Transaction transaction = null;
        Long count = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            count = routeMapDao.count(
                    "SELECT COUNT(e) FROM " + RouteMap.class.getSimpleName() + " e " +
                            "WHERE e.name = :name",
                    new AbstractDao.StrParam("name", checkName));

            transaction.commit();
            transaction = null;
            return count;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }


//    @Override
//    public String create(RouteMapInReq routeMapInReq, String googleApiKey, User user) throws Exception {
//        Transaction transaction = null;
//        Session session = null;
//        RouteMap routeMap = ApiUtil.remapRouteJsonToDtoForSave(routeMapInReq, user);
//
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            routeMapDao.save(routeMap);
//            int countOfGeofence = 0;
//            if (routeMapInReq.getWayPoints() != null) {
//                int i = 0;
//                List<WayPoint> googleWayPoints = new ArrayList<>();
//                for (WayPoint wayPoint : routeMapInReq.getWayPoints()) {
//                    wayPoint.setNumber(i);
//                    if (wayPoint.getLat() == null || wayPoint.getLng() == null) {
//                        throw new IllegalArgumentException("Coordinates cannot be null.");
//                    }
//
//                    if (wayPoint.getZoneType() == null) {
//                        googleWayPoints.add(wayPoint);
//                    } else {
//                        if ((wayPoint.getRadius() == null && isBlank(wayPoint.getPolygonHash())) || isBlank(wayPoint.getName())) {
//                            throw new IllegalArgumentException("Null required fields. Name or radius.");
//                        }
//                        Geo geofence = ApiUtil.remapWayPointToGeofance(wayPoint);
//                        geofenceDao.create(geofence);
//                        RouteMapGeofence routeMapGeofence = ApiUtil.remapWayPointToRouteMapGeofence(wayPoint, routeMap, geofence, user);
//                        routeMapGeofenceDao.save(routeMapGeofence);
//
//                        countOfGeofence++;
//                    }
//                    i++;
//                }
//                routeMap.setWaypoints(new GsonBuilder().create().toJson(googleWayPoints));
//                routeMap.setWayPointsQuantity(String.valueOf(i));
//            }
//
//            if (countOfGeofence < 2) {
//                throw new IllegalArgumentException("Count of geofence is not valid.");
//            }
//            routeMap.setRouteSnapshot(createRouteSnapshoot(routeMapInReq));
//            routeMapDao.create(routeMap);
//            transaction.commit();
//            transaction = null;
//
//            return routeMap.getId();
//        } finally {
//            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
//            if (session != null && session.isOpen()) session.close();
//        }
//    }

//    public boolean create(RouteMap routeMap, List<RouteMapGeofence> routeMapGeofenceList) throws Exception {
//        Session session = null;
//        Transaction transaction = null;
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            if (isNotEmpty(routeMap.getId())) {
//                RouteMap prevRouteMap = routeMapDao.findById(routeMap.getId());
//                if (prevRouteMap != null) {
//                    List<RouteMapGeofence> prevRouteMapGeofenceList = routeMapGeofenceDao.find(
//                            "SELECT e FROM " + RouteMapGeofence.class.getSimpleName() + " e " +
//                                    "WHERE e.routeMapId = :routeMapId",
//                            new AbstractDao.StrParam("routeMapId", prevRouteMap.getId()));
//                    routeMapGeofenceDao.delete(prevRouteMapGeofenceList);
//                }
//            }
//
//            routeMapGeofenceDao.save(routeMapGeofenceList);
//            routeMapDao.save(routeMap);
//
//            transaction.commit();
//            transaction = null;
//            return true;
//        } finally {
//            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
//            if (session != null && session.isOpen()) session.close();
//        }
//    }
//    @Override
//    public Boolean delete(String routeMapId) throws Exception {
//        Session session = null;
//        Transaction transaction = null;
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            routeMapDao.delete(new RouteMap(routeMapId));
//            List<RouteMapGeofence> routeMapGeofenceList = routeMapGeofenceDao.find(
//                    "SELECT e FROM " + RouteMapGeofence.class.getSimpleName() + " e " +
//                            "WHERE e.routeMapId = :routeMapId",
//                    new AbstractDao.StrParam("routeMapId", routeMapId));
//            if (routeMapGeofenceList != null) routeMapGeofenceDao.delete(routeMapGeofenceList);
//
//            transaction.commit();
//            transaction = null;
//            return true;
//        } finally {
//            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
//            if (session != null && session.isOpen()) session.close();
//        }
//    }

//    @Override
//    public Boolean create(RouteMapInReq routeMapInReq, User user) throws Exception {
//        Session session = null;
//        Transaction transaction = null;
//
//        try {
//            RouteMap routeMap = ApiUtil.remapRouteJsonToDtoForSave(routeMapInReq, user);
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            List<RouteMapGeofence> routeMapGeofences = routeMapGeofenceDao.find(
//                    "SELECT e FROM " + RouteMapGeofence.class.getSimpleName() + " e " +
//                            "WHERE e.routeMapId = :routeMapId",
//                    new AbstractDao.StrParam("routeMapId", routeMap.getId()));
//            if (routeMapGeofences != null) {
//                for (RouteMapGeofence routeMapGeofence : routeMapGeofences) {
//                    routeMapGeofenceDao.delete(routeMapGeofence);
//                }
//            }
//
//            if (routeMapInReq.getWayPoints() != null) {
//                int i = 0;
//                List<WayPoint> googleWayPoints = new ArrayList<>();
//                for (WayPoint wayPoint : routeMapInReq.getWayPoints()) {
//                    wayPoint.setNumber(i);
//                    if (wayPoint.getZoneType() == null) {
//                        googleWayPoints.add(wayPoint);
//                    } else {
//                        Geo geofence = ApiUtil.remapWayPointToGeofance(wayPoint);
//                        geofenceDao.create(geofence);
//                        RouteMapGeofence routeMapGeofence = ApiUtil.remapWayPointToRouteMapGeofence(wayPoint, routeMap, geofence, user);
//                        routeMapGeofenceDao.save(routeMapGeofence);
//                    }
//                    i++;
//                }
//                routeMap.setWaypoints(new GsonBuilder().create().toJson(googleWayPoints));
//                routeMap.setWayPointsQuantity(String.valueOf(i));
//            }
//
//            routeMapDao.create(routeMap);
//            transaction.commit();
//            transaction = null;
//            return true;
//        } finally {
//            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
//            if (session != null && session.isOpen()) session.close();
//        }
//    }

//    @Override
//    public RouteMap getByRouteMapId(String routeMapId) throws Exception {
//        RouteMap res = null;
//
//        Session session = null;
//        Transaction transaction = null;
//        RouteMapInReq bean = null;
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            res = routeMapDao.findById(routeMapId);
//
//            transaction.commit();
//            transaction = null;
//
//        } finally {
//            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
//            if (session != null && session.isOpen()) session.close();
//        }
//        return res;
//    }

//    @Override
//    public RouteMapInReq getByRouteMapId(String routeMapId, User user) throws Exception {
//        Session session = null;
//        Transaction transaction = null;
//        RouteMapInReq bean = null;
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            RouteMap routeMap = routeMapDao.findById(routeMapId);
//            if (routeMap != null) {
//                bean = ApiUtil.remapRouteMapDtoToRouteJson(routeMap);
//
//                List<RouteMapGeofence> routeMapGeofences = routeMapGeofenceDao.find(
//                        "SELECT e FROM " + RouteMapGeofence.class.getSimpleName() + " e " +
//                                "WHERE e.routeMapId = :routeMapId",
//                        new AbstractDao.StrParam("routeMapId", routeMapId));
//
//                for (RouteMapGeofence routeMapGeofence : routeMapGeofences) {
//                    Geo geofence = geofenceDao.findById(routeMapGeofence.getGeofenceId());
//                    WayPoint wayPoint = ApiUtil.remapGeofenceDataToWayPoint(routeMapGeofence, geofence, user);
//                    bean.getWayPoints().add(wayPoint);
//                }
//            }
//            transaction.commit();
//            transaction = null;
//
//            if (bean != null) {
//                Collections.sort(bean.getWayPoints(), new Comparator<WayPoint>() {
//                    @Override
//                    public int compare(WayPoint o1, WayPoint o2) {
//                        Integer o1Number = Integer.valueOf(o1.getNumber());
//                        Integer o2Number = Integer.valueOf(o2.getNumber());
//                        return o1Number.compareTo(o2Number);
//                    }
//                });
//            }
//        } finally {
//            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
//            if (session != null && session.isOpen()) session.close();
//        }
//        return bean;
//    }
//
//    @Override
//    public RouteMapInReq getRouteRequestInfo(String id, User user) throws Exception {
//        Session session = null;
//        Transaction transaction = null;
//        RouteMapInReq response;
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            Route route = routeDao.findById(id);
//            response = ApiUtil.remapRouteDtoToRouteJson(route);
//
//            transaction.commit();
//            transaction = null;
//            return response;
//        } finally {
//            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
//            if (session != null && session.isOpen()) session.close();
//        }
//    }
//
//    public RouteMapInReq getRouteForList(String id, User user) {
//        RouteMapInReq bean = null;
//        try {
//            RouteMap routeMap = routeMapDao.findById(id);
//            if (routeMap != null) {
//                bean = ApiUtil.remapRouteMapDtoToRouteJson(routeMap);
//                List<RouteMapGeofence> routeMapGeofences =
//                        routeMapGeofenceDao.find(
//                                "SELECT e FROM " + RouteMapGeofence.class.getSimpleName() + " e " +
//                                        "WHERE e.routeMapId = :routeMapId",
//                                new AbstractDao.StrParam("routeMapId", id));
//
//                for (RouteMapGeofence routeMapGeofence : routeMapGeofences) {
//                    Geo geofence = geofenceDao.findById(routeMapGeofence.getGeofenceId());
//                    WayPoint wayPoint = ApiUtil.remapGeofenceDataToWayPoint(routeMapGeofence, geofence, user);
//                    bean.getWayPoints().add(wayPoint);
//                }
//            }
//            if (bean != null && bean.getWayPoints() != null) {
//                Collections.sort(bean.getWayPoints(), new Comparator<WayPoint>() {
//                    @Override
//                    public int compare(WayPoint o1, WayPoint o2) {
//                        Integer o1Number = Integer.valueOf(o1.getNumber());
//                        Integer o2Number = Integer.valueOf(o2.getNumber());
//                        return o1Number.compareTo(o2Number);
//                    }
//                });
//            }
//
//        } catch (Exception e) {
//            LogUtil.error(RouteMapServiceImpl.class.getName(), e, "Error on getting route with id: " + id);
//            bean = null;
//        }
//        return bean;
//    }
}
