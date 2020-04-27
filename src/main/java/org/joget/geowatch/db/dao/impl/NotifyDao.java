package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.Notify;

/**
 * Created by k.lebedyantsev
 * Date: 2/24/2018
 * Time: 10:03 AM
 */
public class NotifyDao extends AbstractDao<Notify> implements Dao<Notify> {
    public NotifyDao() {
        super(Notify.class);
    }
}
