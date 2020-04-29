package org.joget.geowatch.processing.analize.impl;

import static org.joget.geowatch.type.EventSubType.NONSENSE;
import static org.joget.geowatch.type.EventSubType.SOMETHING;

import java.util.Date;
import java.util.Map;

import org.joget.geowatch.app.AppContext;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.db.service.NotifyService;
import org.joget.geowatch.db.service.impl.NotifyServiceImpl;
import org.joget.geowatch.processing.analize.Analyzer;
import org.joget.geowatch.processing.analize.dto.AnalyzeTripContext;
import org.joget.geowatch.processing.analize.result.AnalyzeResult;
import org.joget.geowatch.type.EventType;

public class NotReachedToStartingPointAnalizer extends Analyzer {

	private NotifyService notifyService;
	
	public  NotReachedToStartingPointAnalizer() {
		// TODO Auto-generated constructor stub
		super();
		try {
			this.notifyService=AppContext.getBean("notifyService", NotifyServiceImpl.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void analyze(EventType eventType, AnalyzeTripContext tripContext, Log log) throws Exception {
		// TODO Auto-generated method stub
		
		
		Map<EventType, Notify> notifications = notifyService.getLast(log.getTripId(), log.getGhtVehicleId(), new EventType[]{EventType.GEOFENCE_START});
		switch (eventType) {
		case DELAY_START_NEW:
		{
			AnalyzeResult result= new AnalyzeResult(eventType, NONSENSE);
			try {
				Date date=new Date(new Date().getTime()-(60l*60l*1000l));
				//Wait for 60 mins from trip start to get the alert. 
				if(tripContext.getStartDateTime().after(date))
				{
					result= new AnalyzeResult(eventType, NONSENSE);
				}
				else if(notifications.get(EventType.GEOFENCE_START)==null)
				{
				result= new AnalyzeResult(eventType, SOMETHING);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			log.getAnalyzeResult().put(eventType, result);
			
		
			
		}
			
			break;
		default:
			break;
 
		}
	
	}

}
