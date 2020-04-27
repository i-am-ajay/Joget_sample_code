package org.joget.geowatch.api.dto.out.resp;

import org.joget.geowatch.db.dto.RouteMap;
import org.joget.geowatch.db.dto.inner.RouteMapInnerEntity;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;

public class RouteMapOutResp {
    private String id;
    private String name;
    private String polylineHash;
    private WayPointOutResp[] wayPoints;

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

    public String getPolylineHash() {
        return polylineHash;
    }

    public void setPolylineHash(String polylineHash) {
        this.polylineHash = polylineHash;
    }

    public WayPointOutResp[] getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(WayPointOutResp[] wayPoints) {
        this.wayPoints = wayPoints;
    }

    public static RouteMapOutResp[] update(RouteMapOutResp[] itemArr, RouteMapInnerEntity[] routeMapArr) throws Exception {
        if(routeMapArr == null) return itemArr;
        if(itemArr == null) return null;

        for(int i = 0; i < routeMapArr.length; i++)
            itemArr[i] = update(new RouteMapOutResp(), routeMapArr[i]);

        return itemArr;
    }

    public static RouteMapOutResp update(RouteMapOutResp item, RouteMapInnerEntity routeMap) throws Exception {
        if (item == null) return null;
        if (routeMap == null) return item;

        item.id = routeMap.getId();
        item.name = routeMap.getName();
        item.polylineHash = routeMap.getPolylineHash();
        item.wayPoints = WayPointOutResp.update(
                new WayPointOutResp[routeMap.getWayPointList().size()],
                routeMap.getWayPointList().toArray(new WayPointInnerEntity[]{})
        );
        return item;
    }

    public static RouteMapOutResp[] update(RouteMapOutResp[] itemArr, RouteMap[] routeMapArr) throws Exception {
        if(routeMapArr == null) return itemArr;
        if(itemArr == null) return null;

        for(int i = 0; i < routeMapArr.length; i++)
            itemArr[i] = update(new RouteMapOutResp(), routeMapArr[i]);

        return itemArr;
    }

    public static RouteMapOutResp update(RouteMapOutResp item, RouteMap routeMap) throws Exception {
        if (item == null) return null;
        if (routeMap == null) return item;

        item.id = routeMap.getId();
        item.name = routeMap.getName();
        item.polylineHash = routeMap.getPolylineHash();
        item.wayPoints = WayPointOutResp.update(
                new WayPointOutResp[routeMap.getWayPointList().size()],
                routeMap.getWayPointList().toArray(new WayPointInnerEntity[]{})
        );
        return item;
    }
}
