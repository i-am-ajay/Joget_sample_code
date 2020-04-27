package org.joget.geowatch.db.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.service.PropertyService;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Property;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/22/2018
 * Time: 1:13 PM
 */
public class PropertyServiceImpl implements PropertyService {
    private Dao<Property> propertyDao;
    private SessionFactory sessionFactory;

    public PropertyServiceImpl(SessionFactory sessionFactory, Dao propertyDao) {
        this.propertyDao = propertyDao;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Property> listProperties(String classKey) {
        Session session = null;
        Transaction transaction = null;
        List<Property> properties = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            properties = propertyDao.find(
                    "SELECT e FROM " + Property.class.getSimpleName() + " e WHERE e.key LIKE (:key)",
                    new AbstractDao.StrParam("key", "%" + classKey + "%")
            );

            transaction.commit();
            transaction = null;

        } catch (Exception e) {
            LogUtil.error(RouteMapServiceImpl.class.getName(), e, "Error on getting property : " + classKey);

        } finally {
            if (transaction != null && !transaction.wasCommitted()) transaction.rollback();
            if (session != null && session.isOpen()) session.close();
        }

        if (properties != null) {
            for (Property property : properties) {
                property.setKey(property.getKey().replace(classKey + ".", ""));
            }
        }
        return properties;
    }
}
