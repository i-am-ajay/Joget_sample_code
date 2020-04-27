package org.joget.geowatch.util.geo.dto;

import com.google.maps.model.LatLng;

public abstract class Geo {

    public enum Type{ROUTE, POLYGON, CIRCLE}

    public abstract Type getType();

    public abstract boolean isWithin(LatLng point, double tolerance);
}
