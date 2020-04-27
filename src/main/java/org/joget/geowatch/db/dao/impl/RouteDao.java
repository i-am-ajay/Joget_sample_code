package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.Route;

/**
 * Created by k.lebedyantsev
 * Date: 1/22/2018
 * Time: 4:44 PM
 */
public class RouteDao extends AbstractDao<Route> implements Dao<Route> {
    public RouteDao() {
        super(Route.class);
    }
}
