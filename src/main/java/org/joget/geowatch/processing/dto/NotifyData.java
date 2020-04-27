package org.joget.geowatch.processing.dto;

import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.type.EventType;

import java.util.List;
import java.util.Map;

public class NotifyData {

    protected Map<EventType, Notify> lastNotifyEventMap;
    protected List<Notify> notifyList;

    public NotifyData(Map<EventType, Notify> lastNotifyEventMap) {
        this.lastNotifyEventMap = lastNotifyEventMap;
    }

    public List<Notify> getNotifyList() {
        return notifyList;
    }

    public void setNotifyList(List<Notify> notifyList) {
        this.notifyList = notifyList;
    }

    public Map<EventType, Notify> getLastNotifyEventMap() {
        return lastNotifyEventMap;
    }

    public void setLastNotifyEventMap(Map<EventType, Notify> lastNotifyEventMap) {
        this.lastNotifyEventMap = lastNotifyEventMap;
    }
}
