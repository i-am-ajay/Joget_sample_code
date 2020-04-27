package org.joget.geowatch.processing.analize.impl;

import com.google.maps.model.LatLng;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;
import org.joget.geowatch.util.geo.dto.Circle;
import org.joget.geowatch.util.geo.dto.Geo;
import org.joget.geowatch.util.geo.dto.Polygon;

public class ExGeo {
    protected Geo geo;
    protected WayPointInnerEntity wp;

    public ExGeo(WayPointInnerEntity wp) {
        this.wp = wp;
        if (wp.getPolygonHash() != null) this.geo = new Polygon(wp.getPolygonHash());
        else this.geo = new Circle(new LatLng(wp.getLat(), wp.getLng()), wp.getRadius());
    }

    public boolean isWithin(LatLng point, double tolerance) {
        return geo.isWithin(point, tolerance);
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public WayPointInnerEntity getWp() {
        return wp;
    }

    public void setWp(WayPointInnerEntity wp) {
        this.wp = wp;
    }
}
