package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.dto.in.req.RouteMapInReq;
import org.joget.geowatch.api.process.in.RouteInProcess;
import org.joget.geowatch.db.service.RouteMapService;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * Created by k.lebedyantsev
 * Date: 1/11/2018
 * Time: 2:07 PM
 */
public class RouteMapController extends AbstractController {
    private static final String TAG = RouteMapController.class.getSimpleName();

    private RouteInProcess routeInProcess;
    private RouteMapService routeMapService;

    public RouteMapController(RouteInProcess routeInProcess, RouteMapService routeMapService) {
        this.routeInProcess = routeInProcess;
        this.routeMapService = routeMapService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {

        RouteMapInReq routeMapInReq = httpWrap.getBody(RouteMapInReq.class);
        if (routeMapInReq == null) httpWrap.error(SC_BAD_REQUEST, null);

       return routeInProcess.create(httpWrap, user, routeMapInReq);
//        if (isNotBlank(id)) httpWrap.result(SC_OK, new IdOutResp(id));
//        return httpWrap;
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {

        String tripId = httpWrap.getParameter("tripId");
        String routeId = httpWrap.getParameter("routeId");
        String routMapId = httpWrap.getParameter("id");
        String searchMask = httpWrap.getParameter("search");
        String routeRequestInfoId = httpWrap.getParameter("routeRequestId");
        String checkName = httpWrap.getParameter("checkName");


        return routeInProcess.get(httpWrap, user, searchMask, tripId, routeId, routMapId, checkName);

//        if (isNotBlank(routeRequestInfoId)) {
//            RouteMapInReq routeMapInReq = routeMapService.getRouteRequestInfo(routeRequestInfoId, user);
//            httpWrap.result(SC_OK, routeMapInReq != null ? routeMapInReq : "");
//        } else if (isNotBlank(id)) {
//            RouteMapInReq routeMapInReq = routeMapService.getByRouteMapId(id, user);
//            httpWrap.result(SC_OK, routeMapInReq != null ? routeMapInReq : "");
//        } else {
//            List<RouteMapInReq> routeMapInReqList = routeMapService.list(searchMask, user);
//            httpWrap.result(SC_OK, routeMapInReqList != null ? routeMapInReqList : Collections.EMPTY_LIST);
//        }
//        return httpWrap;
    }

    @Override
    public HttpWrap processPut(User user, HttpWrap httpWrap) throws Exception {

        String id = httpWrap.getParameter("id");
        RouteMapInReq routeMapInReq = httpWrap.getBody(RouteMapInReq.class);

        return routeInProcess.update(httpWrap, user, routeMapInReq, id);

//        if (isBlank(id) || routeMapInReq == null) return httpWrap.error(SC_BAD_REQUEST, null);
//
//        routeMapInReq.setId(id);
//        if (routeMapService.create(routeMapInReq, user)) return httpWrap.result(SC_OK, new IdOutResp(id));
//
//        return httpWrap;
    }

    @Override
    public HttpWrap processDelete(User user, HttpWrap httpWrap) throws Exception {

        String id = httpWrap.getParameter("id");

        return routeInProcess.delete(httpWrap, user, id);

//        if (isBlank(id)) httpWrap.error(SC_BAD_REQUEST, null);
//        if (routeMapService.delete(id)) httpWrap.error(SC_OK, new IdOutResp(id));
//
//        return httpWrap;
    }

    protected String getReadPermission() {
        return "route.read";
    }

    protected String getWritePermission() {
        return "route.write";
    }
}
