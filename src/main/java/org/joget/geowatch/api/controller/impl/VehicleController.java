package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.Controller;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.db.dto.type.VehicleType;
import org.joget.geowatch.db.service.GhtVehicleService;
import org.joget.geowatch.db.service.VehicleService;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joget.geowatch.util.StrUtil.getObj;

/**
 * Created by k.lebedyantsev
 * Date: 1/11/2018
 * Time: 2:36 PM
 */
public class VehicleController extends AbstractController implements Controller {
    private static final String TAG = VehicleController.class.getSimpleName();

    private VehicleService vehicleService;
    private GhtVehicleService ghtVehicleService;

    public VehicleController(VehicleService vehicleService, GhtVehicleService ghtVehicleService) {
        this.vehicleService = vehicleService;
        this.ghtVehicleService = ghtVehicleService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {

        String tripId = httpWrap.getParameter("tripId");
        String vehicleType = httpWrap.getParameter("vehicleType");

        if (isBlank(tripId) || isBlank(vehicleType)) httpWrap.result(SC_OK, ghtVehicleService.list());
        else httpWrap.result(SC_OK, vehicleService.getTripVehicle(tripId, getObj(vehicleType, VehicleType.class)));

        return httpWrap;
    }

    @Override
    public HttpWrap processPut(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    @Override
    public HttpWrap processDelete(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    protected String getReadPermission() {
        return "vehicle.read";
    }

    protected String getWritePermission() {
        return "vehicle.write";
    }
}
