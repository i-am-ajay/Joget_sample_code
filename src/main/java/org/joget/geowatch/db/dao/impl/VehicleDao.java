package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.Vehicle;

/**
 * Created by k.lebedyantsev
 * Date: 1/17/2018
 * Time: 3:43 PM
 */
public class VehicleDao extends AbstractDao<Vehicle> implements Dao<Vehicle> {
    public VehicleDao() {
        super(Vehicle.class);
    }
}
