package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.RouteMap;

/**
 * Created by k.lebedyantsev
 * Date: 1/5/2018
 * Time: 12:41 PM
 */
public class RouteMapDao extends AbstractDao<RouteMap> implements Dao<RouteMap> {
    public RouteMapDao() {
        super(RouteMap.class);
    }
}
