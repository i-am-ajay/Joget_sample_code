package org.joget.geowatch.api.process.in;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.dto.in.req.RouteMapInReq;
import org.joget.geowatch.api.dto.in.req.WayPointInReq;
import org.joget.geowatch.api.dto.out.resp.IdOutResp;
import org.joget.geowatch.api.dto.out.resp.RouteMapOutResp;
import org.joget.geowatch.api.dto.out.resp.StatusResp;
import org.joget.geowatch.app.AppProperties;
import org.joget.geowatch.db.dto.RouteMap;
import org.joget.geowatch.db.dto.inner.RouteMapInnerEntity;
import org.joget.geowatch.db.service.RouteMapService;
import org.joget.geowatch.type.WayPointType;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.http.HttpStatus.SC_OK;

public class RouteInProcess {
    private static final String TAG = RouteInProcess.class.getSimpleName();

    private RouteMapService routeMapService;

    public RouteInProcess(RouteMapService routeMapService) {
        this.routeMapService = routeMapService;
    }

    public HttpWrap get(HttpWrap httpWrap, User user, String searchMask, String tripId, String routeId, String routeMapId, String checkName) throws Exception {

        if (searchMask != null) {
            List<RouteMap> routeMapList = routeMapService.list(searchMask);
            RouteMapOutResp[] routeMapOutRespArr = RouteMapOutResp.update(
                    new RouteMapOutResp[routeMapList.size()],
                    routeMapList.toArray(new RouteMap[]{}));
            return httpWrap.result(SC_OK, routeMapOutRespArr);
        }

        if(!isEmpty(tripId)){
            RouteMapInnerEntity routeMap = routeMapService.getByTripId(tripId);
            return httpWrap.result(SC_OK, routeMap);
        }else if(!isEmpty(routeId)){
            RouteMapInnerEntity routeMap = routeMapService.getByRouteId(routeId);
            return httpWrap.result(SC_OK, routeMap);
        } else if (!isEmpty(routeMapId)) {
            RouteMap routeMap = routeMapService.getByRouteMapId(routeMapId);
            return httpWrap.result(SC_OK, routeMap);
        } else  if (!isEmpty(checkName)){
            Long count = routeMapService.checkRouteName(checkName);
            StatusResp resp = count != null && count > 0 ?
                    new StatusResp(200, AppProperties.KEY_ROUTE_EXIST) : new StatusResp(404, AppProperties.KEY_ROUTE_NOT_EXIST);
            return httpWrap.result(SC_OK, resp);
        }
        return httpWrap;
    }


    public HttpWrap update(HttpWrap httpWrap, User user, RouteMapInReq routeMapInReq, String routeMapId) throws Exception {
        if (routeMapInReq == null) return httpWrap.error(SC_BAD_REQUEST, "Route. Body cannot be empty");
        if (isEmpty(routeMapId)) return httpWrap.error(SC_BAD_REQUEST, "Route. routeMapId cannot be empty");
        routeMapInReq.setId(routeMapId);

        if (!isValid(httpWrap, user, routeMapInReq)) return httpWrap;

        RouteMap routeMap = new RouteMap();
        routeMap = RouteMap.update(routeMap, user);
        routeMap = RouteMap.update(routeMap, routeMapInReq);
        boolean res = routeMapService.update(routeMap);
        if (res) return httpWrap.result(SC_OK, new IdOutResp(routeMapId));
        return httpWrap;
    }

    public HttpWrap create(HttpWrap httpWrap, User user, RouteMapInReq routeMapInReq) throws Exception {

        if (!isValid(httpWrap, user, routeMapInReq)) return httpWrap;

        RouteMap routeMap = new RouteMap();
        routeMap = RouteMap.update(routeMap, user);
        routeMap = RouteMap.update(routeMap, routeMapInReq);
        String routeMapId = routeMapService.save(routeMap);
        return httpWrap.result(SC_OK, new IdOutResp(routeMapId));
    }

    public HttpWrap delete(HttpWrap httpWrap, User user, String routeMapId) throws Exception {
        if (isBlank(routeMapId)) httpWrap.error(SC_BAD_REQUEST, null);

        if (routeMapService.delete(routeMapId))
            httpWrap.result(SC_OK, new IdOutResp(routeMapId));

        return httpWrap;
    }

    protected boolean isValid(HttpWrap httpWrap, User user, RouteMapInReq routeMapInReq) throws Exception {
        if (routeMapInReq.getWayPoints() == null || routeMapInReq.getWayPoints().size() < 2) {
            httpWrap.error(SC_BAD_REQUEST, "WayPoints can be 2 ore more");
            return false;
        }

        for (WayPointInReq wpir : routeMapInReq.getWayPoints()) {
            WayPointType wayPointType = wpir.getWayPointType();
            if (wayPointType == null) {
                httpWrap.error(SC_BAD_REQUEST, "Geo. WayPointType cannot be empty");
                return false;
            }
            switch (wayPointType) {
                case WAY_POINT:
                    if (wpir.getLat() == null || wpir.getLng() == null) {
                        httpWrap.error(SC_BAD_REQUEST, "Way Point. Lan & Lng cannot be empty");
                        return false;
                    }
                    break;
                case START:
                case FINISH:
                case GEOFENCE:
                    if (wpir.getLat() == null || wpir.getLng() == null) {
                        httpWrap.error(SC_BAD_REQUEST, "Geo. Lan | Lng cannot be empty");
                        return false;
                    }
                    if (wpir.getRadius() == null && wpir.getPolygonHash() == null) {
                        httpWrap.error(SC_BAD_REQUEST, "Geo. Radius & PolygonHash cannot be empty");
                        return false;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Can't resolve WayPointType:" + wpir.getWayPointType());
            }
        }
        return true;
    }

}
