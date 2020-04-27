package org.joget.geowatch.api.dto.in.req;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/3/2018
 * Time: 1:25 PM
 */
public class RouteMapInReq {
    private String id;
    private String name;
    private String polylineHash;
    private List<WayPointInReq> wayPoints;

    public RouteMapInReq(String id) {
        this.id = id;
    }

    public RouteMapInReq() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WayPointInReq> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<WayPointInReq> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public String getPolylineHash() {
        return polylineHash;
    }

    public void setPolylineHash(String polylineHash) {
        this.polylineHash = polylineHash;
    }

}
