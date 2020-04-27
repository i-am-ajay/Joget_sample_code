package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.Property;

/**
 * Created by k.lebedyantsev
 * Date: 1/22/2018
 * Time: 1:09 PM
 */
public class PropertyDao extends AbstractDao<Property> implements Dao<Property> {
    public PropertyDao() {
        super(Property.class);
    }
}
