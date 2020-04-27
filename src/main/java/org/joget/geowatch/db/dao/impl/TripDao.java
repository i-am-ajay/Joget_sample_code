package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.Trip;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 4:34 PM
 */
public class TripDao extends AbstractDao<Trip> implements Dao<Trip> {
    public TripDao() {
        super(Trip.class);
    }
}
