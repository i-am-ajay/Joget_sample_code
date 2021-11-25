package org.joget.geowatch.api.configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.directory.model.Group;
import org.joget.directory.model.User;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;

import static org.joget.geowatch.app.AppProperties.*;

public class MapActivityApi extends DefaultApplicationPlugin implements PluginWebSupport {

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return MapActivityApi.class.getName();
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return PLUGIN_FULL_NAME + "(MAP Activity API)";
	}

	@Override
	public String getPropertyOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "To get Trip Live Activity ID";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "1" + PLUGIN_FULL_NAME;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return PLUGIN_JOGET_VERSION;
	}

	@Override
	public Object execute(Map arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	 public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 
		 
		  // Get Parameter
        String pid = request.getParameter("trippProId");
        String aid = null;
        
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
       
        WorkflowAssignment assign = workflowManager.getAssignmentByProcess(pid);
        
        System.out.println(assign);
        
        if (assign != null ){
      
	    
						   aid = assign.getActivityId();
                           System.out.println(aid); 
                          
                           System.out.println("/web/userview/GglApp/Monitor/_/TripLiveList?activityId="+aid+"&_mode=assignment");
                           response.sendRedirect("/web/userview/GglApp/Monitor/_/TripLiveList?activityId="+aid+"&_mode=assignment");  
	    
      }else {
        
        response.getWriter().println("No Permission");
      }
        
   
	 }

}
