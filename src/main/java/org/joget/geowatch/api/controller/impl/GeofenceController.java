package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.dto.GeofencesSearchResponse;
import org.joget.geowatch.api.dto.out.resp.IdOutResp;
import org.joget.geowatch.api.dto.WayPoint;
import org.joget.geowatch.api.dto.out.resp.StatusResp;
import org.joget.geowatch.app.AppProperties;
import org.joget.geowatch.db.service.GeofenceService;
import org.joget.geowatch.db.dto.Geofence;

import java.util.Collections;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.http.HttpStatus.SC_OK;
import static org.joget.geowatch.app.AppProperties.GOOGLE_API_KEY;

/**
 * Created by k.lebedyantsev
 * Date: 1/25/2018
 * Time: 5:20 PM
 */
public class GeofenceController extends AbstractController {
    private static final String TAG = GeofenceController.class.getSimpleName();

    private GeofenceService geofenceService;

    public GeofenceController(GeofenceService geofenceService) {
        this.geofenceService = geofenceService;
    }

    @Override
    public HttpWrap processPost(User user, HttpWrap httpWrap) throws Exception {

        Geofence geofence = httpWrap.getBody(Geofence.class);
        String id = geofenceService.save(geofence);
        StatusResp resp = !isBlank(id) ? new StatusResp(200, "Geofence created")
                : new StatusResp(400, "Exist geofence with this name");
        resp.setId(id);
        return httpWrap.result(SC_OK, resp);
    }

    @Override
    public HttpWrap processGet(User user, HttpWrap httpWrap) throws Exception {

        String search = httpWrap.getParameter("search");
        String checkName = httpWrap.getParameter("checkName");
        String searchtype = httpWrap.getParameter("searchtype");

        List<WayPoint> googleTips = null;
        if (search != null && search.length() >= 2)
            googleTips = geofenceService.listGoogleTips(search, GOOGLE_API_KEY);

        if (!isBlank(checkName)){
            Long count = geofenceService.checkGeofenceName(checkName);
            StatusResp resp = count != null && count > 0 ?
                    new StatusResp(200, AppProperties.KEY_GEOFENCE_EXIST) : new StatusResp(404, AppProperties.KEY_GEOFENCE_NOT_EXIST);
            return httpWrap.result(SC_OK, resp);
        }
        
        
        if(searchtype != null) {
        	
        	   List<Geofence> geotypeList = geofenceService.listgeotype(searchtype);
        	   
       
        	   httpWrap.result(SC_OK, new GeofencesSearchResponse(
        			   geotypeList != null ? geotypeList : Collections.EMPTY_LIST,
        		       googleTips != null ? googleTips : Collections.EMPTY_LIST));
        	
        }
       
      else {
            List<Geofence> geofenceList = geofenceService.list(search);
            httpWrap.result(SC_OK, new GeofencesSearchResponse(
                    geofenceList != null ? geofenceList : Collections.EMPTY_LIST,
                    googleTips != null ? googleTips : Collections.EMPTY_LIST));
        }

        return httpWrap;
    }

    @Override
    public HttpWrap processPut(User user, HttpWrap httpWrap) throws Exception {

        Geofence geofence = httpWrap.getBody(Geofence.class);
        Boolean isUpdated = geofenceService.update(geofence);
        if (isUpdated) httpWrap.result(SC_OK, new IdOutResp(geofence.getId()));
        return httpWrap;
    }

    @Override
    public HttpWrap processDelete(User user, HttpWrap httpWrap) throws Exception {

        String geofenceId = httpWrap.getParameter("id");
        if (isBlank(geofenceId)) httpWrap.error(SC_BAD_REQUEST, null);

//        if (geofenceService.isUsed(geofenceId)) return httpWrap.error(SC_FORBIDDEN, geofenseIsUsed);
        if (geofenceService.delete(geofenceId)) return httpWrap.result(SC_OK, new IdOutResp(geofenceId));

        return httpWrap;
    }

    protected String getReadPermission() {
        return "route.read";
    }

    protected String getWritePermission() {
        return "route.write";
    }
}
