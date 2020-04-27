package org.joget.geowatch.processing.analize.result;

import com.google.gson.annotations.Expose;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;
import org.joget.geowatch.type.EventSubType;
import org.joget.geowatch.type.EventType;

public class AnalyzeResult {
    protected EventType eventType;
    protected EventSubType eventSubType;

    @Expose(serialize = false, deserialize = false)
    protected transient WayPointInnerEntity wp;

    public AnalyzeResult(EventType eventType, EventSubType eventSubType) {
        this.eventType = eventType;
        this.eventSubType = eventSubType;
    }

    public AnalyzeResult(EventType eventType, EventSubType eventSubType, WayPointInnerEntity wp) {
        this.eventType = eventType;
        this.eventSubType = eventSubType;
        this.wp = wp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventSubType getEventSubType() {
        return eventSubType;
    }

    public void setEventSubType(EventSubType eventSubType) {
        this.eventSubType = eventSubType;
    }

    public WayPointInnerEntity getWp() {
        return wp;
    }

    public void setWp(WayPointInnerEntity wp) {
        this.wp = wp;
    }
}
