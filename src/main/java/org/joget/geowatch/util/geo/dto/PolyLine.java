package org.joget.geowatch.util.geo.dto;

import com.google.maps.model.LatLng;

import java.util.List;

public class PolyLine {
    public List<LatLng> lineList;

    public PolyLine(List<LatLng> lineList) {
        this.lineList = lineList;
    }
}
