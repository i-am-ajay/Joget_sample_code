package org.joget.geowatch.db.service;

import org.joget.geowatch.db.dto.RouteMap;
import org.joget.geowatch.db.dto.inner.RouteMapInnerEntity;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:55 PM
 */
public interface RouteMapService {

    String save(RouteMap routeMap) throws Exception;

    boolean update(RouteMap routeMap) throws Exception;

    RouteMapInnerEntity getByTripId(String tripId) throws Exception;

    RouteMapInnerEntity getByRouteId(String routeId) throws Exception;

    RouteMap getByRouteMapId(String routeMapId) throws Exception;

    List<RouteMap> list(String searchMask) throws Exception;

    boolean delete(String id) throws Exception;

    Long checkRouteName(String checkName) throws Exception;
}
