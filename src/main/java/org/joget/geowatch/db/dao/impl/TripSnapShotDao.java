package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.TripSnapShot;

public class TripSnapShotDao extends AbstractDao<TripSnapShot> implements Dao<TripSnapShot> {
    public TripSnapShotDao() {
        super(TripSnapShot.class);
    }
}
