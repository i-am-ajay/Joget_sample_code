package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.GhtVehicle;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 2:05 PM
 */
public class GhtVehicleDao extends AbstractDao<GhtVehicle> implements Dao<GhtVehicle> {
    public GhtVehicleDao() {
        super(GhtVehicle.class);
    }
}
