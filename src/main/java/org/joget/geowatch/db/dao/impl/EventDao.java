package org.joget.geowatch.db.dao.impl;

import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.Event;

/**
 * Created by k.lebedyantsev
 * Date: 1/16/2018
 * Time: 2:30 PM
 */
public class EventDao extends AbstractDao<Event> implements Dao<Event> {
    public EventDao() {
        super(Event.class);
    }
}
