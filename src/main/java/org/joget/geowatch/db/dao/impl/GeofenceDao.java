package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.Geofence;

/**
 * Created by k.lebedyantsev
 * Date: 1/20/2018
 * Time: 1:02 PM
 */
public class GeofenceDao extends AbstractDao<Geofence> implements Dao<Geofence> {
    public GeofenceDao() {
        super(Geofence.class);
    }
}
