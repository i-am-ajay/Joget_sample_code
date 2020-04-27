package org.joget.geowatch.api.dto;

import org.joget.geowatch.db.dto.Geofence;

import java.util.Collections;
import java.util.List;

public class GeofencesSearchResponse {

    private List<Geofence> savedGeofences;
    private List<WayPoint> googleTips;

    public GeofencesSearchResponse() {
    }

    public GeofencesSearchResponse(List<Geofence> savedGeofences, List<WayPoint> googleTips) {
        this.savedGeofences = savedGeofences != null ? savedGeofences : Collections.EMPTY_LIST;
        this.googleTips = googleTips != null ? googleTips : Collections.EMPTY_LIST;
    }

    public List<Geofence> getSavedGeofences() {
        return savedGeofences;
    }

    public void setSavedGeofences(List<Geofence> savedGeofences) {
        this.savedGeofences = savedGeofences;
    }

    public List<WayPoint> getGoogleTips() {
        return googleTips;
    }

    public void setGoogleTips(List<WayPoint> googleTips) {
        this.googleTips = googleTips;
    }
}
