package org.joget.geowatch.api.configuration;


import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SecurityUtil;
import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.Controller;
import org.joget.geowatch.api.controller.HttpWrap;
import org.joget.geowatch.api.controller.impl.AbstractController;
import org.joget.geowatch.api.dto.type.ActionType;
import org.joget.geowatch.app.AppContext;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;

import bsh.Console;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joget.geowatch.app.AppProperties.*;

/**
 * Created by klebedyantsev on 2017-12-22
 */
public class MapApi extends DefaultApplicationPlugin implements PluginWebSupport {

    private static final String TAG = MapApi.class.getSimpleName();
    protected static final boolean NOT_CHECK_AUTH = true;

    public String getName() {
        return "0" + PLUGIN_FULL_NAME;
    }

    public String getVersion() {
        return PLUGIN_JOGET_VERSION;
    }

    public String getDescription() {
        return PLUGIN_JOGET_DESCRIPTION;
    }

    public Object execute(Map map) {
        return null;
    }

    public String getLabel() {
        return PLUGIN_FULL_NAME;
    }

    public String getClassName() {
        return MapApi.class.getName();
    }

    public String getPropertyOptions() {
        return null;
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    
    	
    	HttpWrap httpWrap = new HttpWrap(request, response);
        try {
            if (PLUGIN_DEBUG_MODE) {
                LogUtil.info(TAG, "INCOMING METHOD: " + request.getMethod());
                LogUtil.info(TAG, "INCOMING URI: " + request.getRequestURI());
                LogUtil.info(TAG, "INCOMING QUERY: " + request.getQueryString());
                LogUtil.info(TAG, "INCOMING SESSION: " + request.getSession().getId());
            }

            httpWrap = innerWebService(httpWrap);
        } catch (Throwable t) {
            LogUtil.error(TAG, t, "ERROR");
            response.setStatus(SC_INTERNAL_SERVER_ERROR);
        } finally {
            httpWrap.send();
        }
    }
    
    public boolean validate(String token)
    {
    	FormDataDao formDatadao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
		FormRow row=formDatadao.load("links","share_link", SecurityUtil.decrypt(token));
		if(row==null)
		return false;
		
    	return true;
    }
    
    private HttpWrap innerWebService(HttpWrap httpWrap) throws Exception {

        String action = httpWrap.getParameter("action");
        if (isBlank(action)) return httpWrap.error(SC_BAD_REQUEST, null);

        WorkflowUserManager wfum = AppContext.getBean("workflowUserManager", WorkflowUserManager.class);
       

        if(!isBlank(httpWrap.getParameter("token")))
        {	if(validate(httpWrap.getParameter("token"))==false)
        	 return httpWrap.error(SC_UNAUTHORIZED, null);
           wfum.setCurrentThreadUser("guest");
        }
        else
        if (WorkflowUtil.isCurrentUserAnonymous() && !action.equals("LifeTrip")) {
            return httpWrap.error(SC_UNAUTHORIZED, null);
        }
        User user = wfum.getCurrentUser();
        
     
        Controller controller;
        switch (ActionType.valueOf(action)) {
            case Route:
                controller = AppContext.getBean("routeController", Controller.class);
                break;
            case Vehicle:
                controller = AppContext.getBean("vehicleController", Controller.class);
                break;
            case LifeTrip:
                controller = AppContext.getBean("tripController", Controller.class);
                break;
            case GhtLog:
                controller = AppContext.getBean("ghtLogController", Controller.class);
                break;
            case Alert:
                controller = AppContext.getBean("alertController", Controller.class);
                break;
            case LifeTripGeoData:
                controller = AppContext.getBean("geoDataController", Controller.class);
                break;
            case Property:
                controller = AppContext.getBean("propertyController", Controller.class);
                break;
            case Geofence:
                controller = AppContext.getBean("geofenceController", Controller.class);
                break;
            case Notify:
                controller = AppContext.getBean("notifyController", Controller.class);
                break;
            case TripTest:
                controller = AppContext.getBean("tripTestController", Controller.class);
                break;
            case FinishedTrip:
                controller = AppContext.getBean("finishedTripController", Controller.class);
                break;
            default:
                return httpWrap.error(SC_BAD_REQUEST, null);
        }

        String method = httpWrap.getMethod();
        if (!canUse(method, user, (AbstractController) controller)) return httpWrap.error(SC_UNAUTHORIZED, null);

        switch (method) {
            case "POST":
                return controller.processPost(user, httpWrap);
            case "PUT":
                return controller.processPut(user, httpWrap);
            case "GET":
                return controller.processGet(user, httpWrap);
            case "DELETE":
                return controller.processDelete(user, httpWrap);
        }
        return httpWrap;
    }

    protected boolean canUse(String method, User user, AbstractController controller) {
        if (NOT_CHECK_AUTH) return true;
        if (WorkflowUtil.isCurrentUserInRole("ROLE_ADMIN")) return true;

        switch (method) {
            case "POST":
            case "PUT":
            case "DELETE":
                return controller.canWrite(user);
            case "GET":
                return controller.canRead(user);
            default:
                return false;
        }
    }
}
