package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dao.impl.AbstractDao;
import org.joget.geowatch.db.dto.Log;

/**
 * Created by k.lebedyantsev
 * Date: 1/16/2018
 * Time: 2:24 PM
 */
public class LogDao extends AbstractDao<Log> implements Dao<Log> {
    public LogDao() {
        super(Log.class);
    }
}
