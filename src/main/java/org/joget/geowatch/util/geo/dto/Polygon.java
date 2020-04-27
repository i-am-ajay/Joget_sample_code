package org.joget.geowatch.util.geo.dto;

import com.google.maps.model.LatLng;
import org.joget.geowatch.util.geo.PolyUtil;
import java.util.List;

import static org.joget.geowatch.util.geo.dto.Geo.Type.POLYGON;

public class Polygon extends Geo {
    public List<LatLng> lineList;

    public Polygon(String polylineHash) {
        lineList = PolyUtil.decode(polylineHash);
    }

    @Override
    public Type getType() {
        return POLYGON;
    }

    @Override
    public boolean isWithin(LatLng point, double tolerance) {
        return PolyUtil.containsLocation(point, lineList, true);
    }
}
