package org.joget.geowatch.util.geo;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import org.joget.geowatch.util.geo.dto.Polygon;

import java.util.Arrays;
import java.util.List;

public class PolyUtil2 {

    public static boolean containsLocation(LatLng point, Polygon polygon, boolean geodesic) {
        return PolyUtil.containsLocation(point, polygon.lineList, geodesic);
    }

    public static boolean isLocationOnPath(LatLng latLng, List<LatLng> lineList, boolean geodesic, double tolerance) {
        boolean flagOnPolyLine = false;
        LatLng[] polylineArr = new LatLng[2];
        for (int i = 0; i < (lineList.size() - 1); i++) {
            polylineArr[0] = lineList.get(i);
            polylineArr[1] = lineList.get(i + 1);
            if (PolyUtil.isLocationOnPath(latLng, Arrays.asList(polylineArr), geodesic, tolerance)) {
                flagOnPolyLine = true;
                break;
            }
        }
        return flagOnPolyLine;
    }

    public static List<LatLng> getPolyLine(String polylineHash) throws Exception {
        EncodedPolyline encoder = new EncodedPolyline(polylineHash);
        List<LatLng> lineList = encoder.decodePath();
        if (lineList == null || lineList.size() < 2)
            throw new IllegalAccessException("The polylineHash is invalid");
        return lineList;
    }
}
