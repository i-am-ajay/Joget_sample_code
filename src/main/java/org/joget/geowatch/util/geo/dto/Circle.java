package org.joget.geowatch.util.geo.dto;

import com.google.maps.model.LatLng;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static org.joget.geowatch.util.geo.dto.Geo.Type.CIRCLE;

public class Circle extends Geo {
    private LatLng p;
    private Double r;

    public Circle(LatLng p, Double r) {
        this.p = p;
        this.r = r;
    }

    @Override
    public Type getType() {
        return CIRCLE;
    }

    @Override
    public boolean isWithin(LatLng point, double tolerance) {

        double lat1 = point.lat;
        double lat2 = p.lat;

        double lng1 = point.lng;
        double lng2 = p.lng;

        double nr = 2 * asin(sqrt(pow(sin(toRadians((lat1 - lat2) / 2)), 2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                        pow(sin(toRadians((lng1 - lng2) / 2)), 2))) * 6378245;

        try {
        double rt = r + tolerance;
        return nr < rt;
        }
        catch (Exception e) {
			// TODO: handle exception
        	System.out.println("Point .."+p.toUrlValue() +" point 2"+point.toUrlValue() +" radious "+r);
		}
       
        return false;
    }
}
