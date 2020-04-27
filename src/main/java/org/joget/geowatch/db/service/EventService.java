package org.joget.geowatch.db.service;

import org.joget.geowatch.db.dto.Event;
import org.joget.geowatch.type.EventType;

import java.util.Collection;
import java.util.Map;

public interface EventService {
    Map<EventType, Event> getLast(String tripId, String ghtVehicle, EventType[] eventTypeArr) throws Exception;

    void save(Collection<Event> eventList);

    String save(Event event) throws Exception;
}
