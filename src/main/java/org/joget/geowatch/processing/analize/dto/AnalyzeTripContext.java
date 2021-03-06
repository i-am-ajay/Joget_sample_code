package org.joget.geowatch.processing.analize.dto;

import org.joget.commons.util.LogUtil;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;
import org.joget.geowatch.db.service.LogService;
import org.joget.geowatch.processing.analize.impl.ExGeo;
import org.joget.geowatch.util.geo.dto.Route;
import org.joget.geowatch.db.dto.Geofence;
import org.apache.commons.lang3.StringUtils;
import org.joget.geowatch.type.ZoneType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.joget.geowatch.type.WayPointType.WAY_POINT;
import static org.joget.geowatch.util.DateUtil.getJogetDate;

public class AnalyzeTripContext {
    private static final String TAG = AnalyzeTripContext.class.getSimpleName();

    protected Route route;
    protected Date startDateTime;
    protected Date finishDateTime;
    protected List<ExGeo> geoList;
    protected List<Geofence> blackListed;
    protected List<ExGeo> blackListedGeo;
    
    protected List<Geofence> alertzones;
    protected List<ExGeo> alertzonegeos;
    
    protected List<ExGeo> allalertzonegeos;
    protected List<Geofence> allalertzones;
    
    
  



	public List<ExGeo> getAllalertzonegeos() {
		return allalertzonegeos;
	}

	public void setAllalertzonegeos(List<ExGeo> allalertzonegeos) {
		this.allalertzonegeos = allalertzonegeos;
	}

	public List<Geofence> getAllalertzones() {
		return allalertzones;
	}

	public void setAllalertzones(List<Geofence> allalertzones) {
		this.allalertzones = allalertzones;
	}

	public void setBlackListed(List<Geofence> blackListed) {
		this.blackListed = blackListed;
	}

	public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getFinishDateTime() {
        return finishDateTime;
    }

    public void setFinishDateTime(Date finishDateTime) {
        this.finishDateTime = finishDateTime;
    }

    public List<ExGeo> getGeoList() {
        return geoList;
    }

    public void setGeoList(List<ExGeo> geoList) {
        this.geoList = geoList;
    }
    
    
 public List<ExGeo> getBlackListedGeo() {
    	
    	if(this.blackListedGeo==null)
    	{
    		this.blackListedGeo=new ArrayList<>();
    		for(Geofence fence:this.blackListed)
        	{
        		WayPointInnerEntity ent=new WayPointInnerEntity();
        		ent.setId(fence.getId());
        		try {
        		ent.setLat(Double.parseDouble(fence.getLat()));
        		}
        		catch(Exception e)
        		{
        			e.printStackTrace();
        			continue;
        		}
        		try {
        			ent.setLng(Double.parseDouble(fence.getLng()));
        		}
        		catch(Exception e)
        		{
        			continue;
        		}
        		try {
        			
        			ent.setRadius(Double.parseDouble(fence.getRadius()));
        			//System.out.println("RADIUS --"+ent.getRadius());
        		}
        		catch(Exception e)
        		{
        			//System.out.println("RADIUS --"+fence.getRadius());
        			e.printStackTrace();
        		}
        		
        		if(!StringUtils.isEmpty(fence.getPolygonHash()))
        		ent.setPolygonHash(fence.getPolygonHash());
        		
        		ent.setZoneType(ZoneType.ALERT_ZONE);
        		ent.setName(fence.getName());
        		ent.setAddress(fence.getAddress());
        		
        		ExGeo geo=new ExGeo(ent);
        		if(geo!=null)
        		{
        		if(fence.getRadius()!=null||fence.getPolygonHash()!=null)
        		blackListedGeo.add(geo);
        		}
        	}
    	
    	}
    
		return blackListedGeo;
	}
    
    public List<Geofence> getAlertzones() {
 		return alertzones;
 	}

 	public void setAlertzones(List<Geofence> alertzones) {
 		this.alertzones = alertzones;
 	}

 	public List<ExGeo> getAlertzonegeos() {
 		
 		if(this.alertzonegeos==null)
    	{
    		this.alertzonegeos=new ArrayList<>();
    		for(Geofence fence:this.alertzones)
        	{
        		WayPointInnerEntity ent=new WayPointInnerEntity();
        		ent.setId(fence.getId());
        		try {
        		ent.setLat(Double.parseDouble(fence.getLat()));
        		}
        		catch(Exception e)
        		{
        			e.printStackTrace();
        			continue;
        		}
        		try {
        			ent.setLng(Double.parseDouble(fence.getLng()));
        		}
        		catch(Exception e)
        		{
        			continue;
        		}
        		try {
        			
        			
        			ent.setRadius(Double.parseDouble(fence.getRadius()));
        			//System.out.println("RADIUS --"+ent.getRadius());
        		}
        		catch(Exception e)
        		{
        			e.printStackTrace();
        		}
        		
        		if(!StringUtils.isEmpty(fence.getPolygonHash()))
        			ent.setPolygonHash(fence.getPolygonHash());
        		
        		ent.setZoneType(ZoneType.ALERT_ZONE);
        		ent.setName(fence.getName());
        		ent.setAddress(fence.getAddress());
        		
        		ExGeo geo=new ExGeo(ent);
        		if(geo!=null)
        		{
        		if(fence.getRadius()!=null||fence.getPolygonHash()!=null)
        		alertzonegeos.add(geo);
        		}
        	}
    	
    	}

 		return alertzonegeos;
 	}
 	
 	//List of all zone geopoints
 	
public List<ExGeo> getAllAlertzonegeos() {
	
	
	if(this.allalertzonegeos==null)
	{
		this.allalertzonegeos=new ArrayList<>();
		for(Geofence fence:this.allalertzones)
    	{
    		WayPointInnerEntity ent=new WayPointInnerEntity();
    		ent.setId(fence.getId());
    		try {
    		ent.setLat(Double.parseDouble(fence.getLat()));
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    			continue;
    		}
    		try {
    			ent.setLng(Double.parseDouble(fence.getLng()));
    		}
    		catch(Exception e)
    		{
    			continue;
    		}
    		try {
    			
    			
    			ent.setRadius(Double.parseDouble(fence.getRadius()));
    			//System.out.println("RADIUS --"+ent.getRadius());
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		
    		if(!StringUtils.isEmpty(fence.getPolygonHash()))
    			ent.setPolygonHash(fence.getPolygonHash());
    		
    		ent.setZoneType(ZoneType.ALERT_ZONE);
    		ent.setName(fence.getName());
    		ent.setAddress(fence.getAddress());
    		
    		ExGeo geo=new ExGeo(ent);
    		if(geo!=null)
    		{
    		if(fence.getRadius()!=null||fence.getPolygonHash()!=null)
    		allalertzonegeos.add(geo);
    		}
    	}
	
	}
 		
 		 
 		return  allalertzonegeos;
 	}
 	

    public static AnalyzeTripContext getInstance(Trip trip) throws Exception {
        AnalyzeTripContext item = new AnalyzeTripContext();
        item.route = new Route(trip.getImageRouteMap().getPolylineHash());
        item.startDateTime = getJogetDate(trip.getStartDateTime());
        item.finishDateTime = getJogetDate(trip.getFinishDateTime());

        item.geoList = new ArrayList<>();
        for (int i = 0; i < trip.getImageRouteMap().getWayPointList().size(); i++) {
            try {
                WayPointInnerEntity wp = trip.getImageRouteMap().getWayPointList().get(i);
                if (wp.getWayPointType() == WAY_POINT) continue;

                item.geoList.add(new ExGeo(wp));

            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR.");
            }
        }
        return item;
    }

}
