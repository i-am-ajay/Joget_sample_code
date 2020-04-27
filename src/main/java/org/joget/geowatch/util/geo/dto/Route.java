package org.joget.geowatch.util.geo.dto;

import com.google.maps.model.LatLng;
import java.util.List;

import static org.joget.geowatch.app.AppProperties.ANALYZE_ROUTE_TOLERANCE;
import static org.joget.geowatch.util.geo.PolyUtil.isLocationOnPath;
import static org.joget.geowatch.util.geo.PolyUtil2.getPolyLine;
import static org.joget.geowatch.util.geo.dto.Geo.Type.ROUTE;

public class Route extends Geo {
    protected List<LatLng> lineList;

    public Route(String polylineHash) throws Exception {
        this.lineList = getPolyLine(polylineHash);
    }

    @Override
    public Type getType() {
        return ROUTE;
    }

    @Override
    public boolean isWithin(LatLng point, double tolerance) {
        return isLocationOnPath(point, lineList, true, ANALYZE_ROUTE_TOLERANCE);
    }
}
