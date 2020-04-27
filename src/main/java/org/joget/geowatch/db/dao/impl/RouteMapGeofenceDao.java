package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.RouteMapGeofence;

/**
 * Created by k.lebedyantsev
 * Date: 1/20/2018
 * Time: 1:04 PM
 */
public class RouteMapGeofenceDao extends AbstractDao<RouteMapGeofence> implements Dao<RouteMapGeofence> {
    public RouteMapGeofenceDao() {
        super(RouteMapGeofence.class);
    }
}
