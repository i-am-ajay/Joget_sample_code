package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.db.service.PropertyService;
import org.joget.geowatch.db.dto.Property;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by k.lebedyantsev
 * Date: 1/22/2018
 * Time: 1:22 PM
 */
public class PropertyController extends AbstractController {
    private static final String TAG = PropertyController.class.getSimpleName();
    private PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {
        return httpWrap.error(SC_BAD_REQUEST, null);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {

        String classKey = httpWrap.getParameter("classKey");
        if (isBlank(classKey)) return httpWrap.error(SC_BAD_REQUEST, null);
        List<Property> res = propertyService.listProperties(classKey);
        if (res != null) httpWrap.result(SC_OK, res);
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
        return "";
    }

    protected String getWritePermission() {
        return "";
    }
}
