package org.joget.geowatch.processing;

import org.joget.geowatch.db.dto.Event;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.service.EventService;
import org.joget.geowatch.processing.dto.EventData;
import org.joget.geowatch.processing.dto.LogData;
import org.joget.geowatch.processing.dto.VehicleProcessData;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventSubType;
import org.joget.geowatch.type.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.joget.geowatch.db.dto.type.VehicleType.HAULIER;
import static org.joget.geowatch.db.dto.type.VehicleType.RMO;
import static org.joget.geowatch.db.dto.type.VehicleType.TRAILER;
import static org.joget.geowatch.type.EventSubType.NONSENSE;
import static org.joget.geowatch.type.EventSubType.UNKNOWN;

public class EventProcess {
    private static final String TAG = EventProcess.class.getSimpleName();

    protected EventService eventService;

    public EventProcess(EventService eventService) {
        this.eventService = eventService;
    }

    public void process(VehicleProcessData vehicleProcessData) throws Exception {

        LogData logData;
        Map<EventType, Event> lastEventMap;

        logData = vehicleProcessData.getNewLogData(HAULIER);
        if (logData != null) {
            lastEventMap = eventService.getLast(logData.getTripId(), logData.getGhtVehicleId(), EventType.values());
            vehicleProcessData.addNewEventData(HAULIER,
                    new EventData(process(logData, lastEventMap), lastEventMap));
        }

        logData = vehicleProcessData.getNewLogData(TRAILER);
        if (logData != null) {
            lastEventMap = eventService.getLast(logData.getTripId(), logData.getGhtVehicleId(), EventType.values());
            vehicleProcessData.addNewEventData(TRAILER,
                    new EventData(process(logData, lastEventMap), lastEventMap));
        }

        logData = vehicleProcessData.getNewLogData(RMO);
        if (logData != null) {
            lastEventMap = eventService.getLast(logData.getTripId(), logData.getGhtVehicleId(), EventType.values());
            vehicleProcessData.addNewEventData(RMO,
                    new EventData(process(logData, lastEventMap), lastEventMap));
        }
    }

    public Map<EventType, List<Event>> process(LogData logData, Map<EventType, Event> lastEventMap) throws Exception {
        if (logData == null) return null;
        if (logData.getLogList() == null || logData.getLogList().size() == 0) return null;

        return process(new HashMap<EventType, List<Event>>(), lastEventMap, logData.getLogList());
    }

    protected Map<EventType, List<Event>> process(Map<EventType, List<Event>> eventMap, Map<EventType, Event> lastEventMap, List<Log> logList) {
        for (EventType eventType : EventType.values()) {
            eventMap.put(eventType, process(
                    new ArrayList<Event>(), eventType, lastEventMap.get(eventType), logList));
        }
        return eventMap;
    }

    protected List<Event> process(List<Event> eventList, EventType eventType, Event lastEvent, List<Log> logList) {
        if (logList != null) for (Log log : logList) {
            Event event = process(eventType, lastEvent, log);
            if (event != null) {
                eventList.add(event);
                lastEvent = event;
            }
        }
        return eventList;
    }

    protected Event process(EventType eventType, Event lastEvent, Log log) {

        if (lastEvent != null && lastEvent.getDate().after(log.getDate()))       // hack
            return null;

        AnalyzeResult analyzeResult = log.getAnalyzeResult().get(eventType);
        EventSubType logEventSubType = analyzeResult != null
                ? analyzeResult.getEventSubType()
                : UNKNOWN;

        if (logEventSubType == UNKNOWN) return null;

        EventSubType lastEventSubType = lastEvent != null
                ? lastEvent.getEventSubType()
                : NONSENSE;

        if (logEventSubType == lastEventSubType) return null;
        return Event.update(new Event(), log, eventType, logEventSubType, analyzeResult.getWp());
    }
}

