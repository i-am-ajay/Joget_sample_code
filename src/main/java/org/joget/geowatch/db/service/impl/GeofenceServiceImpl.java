package org.joget.geowatch.db.service.impl;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joget.geowatch.util.UrlUtil.getObj;
import static org.joget.geowatch.util.UrlUtil.Method.GET;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.geowatch.api.dto.GooglePlaceResponse;
import org.joget.geowatch.api.dto.WayPoint;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Geofence;
import org.joget.geowatch.db.dto.RouteMapGeofence;
import org.joget.geowatch.db.service.GeofenceService;
import org.joget.geowatch.util.UrlUtil;

/**
 * Created by k.lebedyantsev
 * Date: 1/25/2018
 * Time: 5:33 PM
 */
public class GeofenceServiceImpl implements GeofenceService {
    private static final String TAG = GeofenceServiceImpl.class.getSimpleName();

    private static final String GOOGLE_BASE_URL = "https://maps.googleapis.com";
    private static final String GOOGLE_PLACE_URL =
            GOOGLE_BASE_URL + "/maps/api/place/autocomplete/json?types=geocode&key=%s&input=%s";

    private SessionFactory sessionFactory;
    private Dao<Geofence> geofenceDao;
    private Dao<RouteMapGeofence> routeMapGeofenceDao;

    public GeofenceServiceImpl(SessionFactory sessionFactory, Dao<Geofence> geofenceDao, Dao<RouteMapGeofence> routeMapGeofenceDao) {
        this.sessionFactory = sessionFactory;
        this.geofenceDao = geofenceDao;
        this.routeMapGeofenceDao = routeMapGeofenceDao;
    }

    @Override
    public String save(Geofence geofence) throws Exception {
        String res = null;
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Geofence.update(geofence);

            Long count = geofenceDao.count(
                    "SELECT COUNT(e) FROM " + Geofence.class.getSimpleName() + " e " +
                            "WHERE e.name = :name",
                    new AbstractDao.StrParam("name", geofence.getName()));
            if (count != null && count == 0) {
                res =  geofenceDao.save(geofence);
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
    public boolean update(Geofence geofence) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            session.setDefaultReadOnly(false);

            Geofence.update(geofence);
            geofenceDao.update(geofence);

            transaction.commit();
            transaction = null;
            return true;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }

//    @Override
//    public Boolean isUsed(String geofenceId) throws Exception {
//        Session session = null;
//        Transaction transaction = null;
//
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            List<RouteMapGeofence> routeMapGeofences = routeMapGeofenceDao.find(
//                    "SELECT e FROM " + RouteMapGeofence.class.getSimpleName() + " e " +
//                            "WHERE e.geofenceId = :geofenceId",
//                    new AbstractDao.StrParam("geofenceId", geofenceId));
//            transaction.commit();
//            transaction = null;
//            return (routeMapGeofences != null && routeMapGeofences.size() > 0);
//        } finally {
//            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
//            if (session != null && session.isOpen()) session.close();
//        }
//    }


    @Override
    public boolean delete(String geofenceId) throws Exception {

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            geofenceDao.delete(new Geofence(geofenceId));

            transaction.commit();
            transaction = null;
            return true;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }
   //old code
  @Override
    public List<Geofence> list(String search) throws Exception {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            List<Geofence> geofences;
            if (isBlank(search)) {
                geofences = geofenceDao.find(
                        "SELECT e FROM " + Geofence.class.getSimpleName() + " e ",
                        "e.name", false);
            } else {
                geofences = geofenceDao.find(
                        "SELECT e FROM " + Geofence.class.getSimpleName() + " e " +
                                "WHERE LOWER(e.name) LIKE LOWER(:search) " +
                                "OR  LOWER(e.address) LIKE LOWER(:search) " +
                                "OR LOWER(e.lng) LIKE LOWER(:search) " +
                                "OR LOWER(e.lat) LIKE LOWER(:search) ",
                        "e.name", false,
                        new AbstractDao.StrParam("search", "%" + search + "%")
                );
            }
            transaction.commit();
            transaction = null;
            return geofences;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }
    
    //new code
    
  @Override
  public List<Geofence> listgeotype(String searchtype) throws Exception {
      Session session = null;
      Transaction transaction = null;
      
      StringBuilder aQuery =
      new StringBuilder("SELECT e FROM "+Geofence.class.getSimpleName()+" e ");
      HashMap parameterListMap =  new HashMap();
	    
	    String str[] = searchtype.split(","); 
  	    List<String> al = new ArrayList<String>();
  	    al = Arrays.asList(str);
      

      try {
          session = sessionFactory.getCurrentSession();
          transaction = session.beginTransaction();

          List<Geofence> geofences;
          if (isBlank(searchtype)) {
              geofences = geofenceDao.find(
                      "SELECT e FROM " + Geofence.class.getSimpleName() + " e ",
                      "e.zoneType", false);
          } else {
        	  
        	 
        	  
        	  buildCollectionCriterion(aQuery, parameterListMap, "searchtype", al);
        	  Query query = session.createQuery(aQuery.toString());  
        	  query.setParameterList("searchtype", al);
           	  geofences = query.list();
 
          }
          transaction.commit();
          transaction = null;
          return geofences;
      } finally {
          if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
          if (session != null && session.isOpen()) session.close();
      }
  }
    public List<WayPoint> listGoogleTips(String search, String googleApiKey) throws Exception {
        List<WayPoint> res = new ArrayList<>();

        UrlUtil.RespResult<GooglePlaceResponse, Object> respRes =
                getObj(GooglePlaceResponse.class, null,
                        GET, GOOGLE_PLACE_URL, new String[]{googleApiKey, search},
                        null, null);

        if (HTTP_OK == respRes.getStatus()) {
            res = WayPoint.create(respRes.getResultObj().getPredictions(), res);
        } else throw new ConnectException();
        return res;
    }

    @Override
    public Long checkGeofenceName(String checkName) throws Exception {
        Session session = null;
        Transaction transaction = null;
        Long count = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            count = geofenceDao.count(
                    "SELECT COUNT(e) FROM " + Geofence.class.getSimpleName() + " e " +
                            "WHERE LOWER(e.name) LIKE LOWER(:name)",
                    new AbstractDao.StrParam("name", "%" + checkName + "%"));

            transaction.commit();
            transaction = null;
            return count;
        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }
    }
    
    
    //new 
    
	protected void buildCollectionCriterion(StringBuilder aQuery, Map parameterListMap, String aFieldName,
			Collection aList) {
		if (aList != null && !aList.isEmpty()) {
			aQuery.append(" WHERE  e.zoneType ").append("in :").append(aFieldName);
			parameterListMap.put(aFieldName, aList);
		}
	}
    
    
}
