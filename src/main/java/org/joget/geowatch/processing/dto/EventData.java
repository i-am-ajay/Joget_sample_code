package org.joget.geowatch.processing.dto;

import org.joget.geowatch.db.dto.Event;
import org.joget.geowatch.type.EventType;

import java.util.List;
import java.util.Map;

public class EventData {

    protected Map<EventType, List<Event>> newEventMap;
    protected Map<EventType, Event> lastEventMap;

    public EventData(Map<EventType, List<Event>> newEventMap, Map<EventType, Event> lastEventMap) {
        this.newEventMap = newEventMap;
        this.lastEventMap = lastEventMap;
    }

    public Map<EventType, Event> getLastEventMap() {
        return lastEventMap;
    }

    public void setLastEventMap(Map<EventType, Event> lastEventMap) {
        this.lastEventMap = lastEventMap;
    }

    public Map<EventType, List<Event>> getNewEventMap() {
        return newEventMap;
    }

    public void setNewEventMap(Map<EventType, List<Event>> newEventMap) {
        this.newEventMap = newEventMap;
    }
}
