package org.joget.geowatch.processing;

import org.joget.geowatch.db.dto.Event;
import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.db.dto.type.NotifyResolveStatusType;
import org.joget.geowatch.db.service.NotifyService;
import org.joget.geowatch.processing.dto.EventData;
import org.joget.geowatch.processing.dto.NotifyData;
import org.joget.geowatch.processing.dto.VehicleProcessData;
import org.joget.geowatch.type.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.joget.geowatch.db.dto.type.VehicleType.HAULIER;
import static org.joget.geowatch.db.dto.type.VehicleType.RMO;
import static org.joget.geowatch.db.dto.type.VehicleType.TRAILER;
import static org.joget.geowatch.type.EventSubType.SOMETHING;

public class NotifyProcess {
    private static final String TAG = NotifyProcess.class.getSimpleName();

    protected NotifyService notifyService;

    public NotifyProcess(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    public void process(VehicleProcessData vehicleProcessData) throws Exception {

        NotifyData notifyData;
        EventData eventData;

        eventData = vehicleProcessData.getNewEventData(HAULIER);
        if (eventData != null) {
            notifyData = new NotifyData(notifyService.getLast(
                    vehicleProcessData.getTrip().getId(),
                    vehicleProcessData.getTrip().getHaulierGhtVehicle().getId(),
                    EventType.values()));

            notifyData.setNotifyList(process(notifyData, eventData));
            vehicleProcessData.addNewNotifyData(HAULIER, notifyData);
        }

        eventData = vehicleProcessData.getNewEventData(TRAILER);
        if (eventData != null) {
            notifyData = new NotifyData(notifyService.getLast(
                    vehicleProcessData.getTrip().getId(),
                    vehicleProcessData.getTrip().getHaulierTrailerGhtVehicle().getId(),
                    EventType.values()));

            notifyData.setNotifyList(process(notifyData, eventData));
            vehicleProcessData.addNewNotifyData(TRAILER, notifyData);
        }

        eventData = vehicleProcessData.getNewEventData(RMO);
        if (eventData != null) {
            notifyData = new NotifyData(notifyService.getLast(
                    vehicleProcessData.getTrip().getId(),
                    vehicleProcessData.getTrip().getRmoGhtVehicle().getId(),
                    EventType.values()));

            notifyData.setNotifyList(process(notifyData, eventData));
            vehicleProcessData.addNewNotifyData(RMO, notifyData);
        }
    }

    public List<Notify> process(NotifyData notifyData, EventData eventData) throws Exception {
        if (eventData.getNewEventMap() == null) return null;
        return process(notifyData.getLastNotifyEventMap(), eventData.getNewEventMap());
    }

    protected List<Notify> process(Map<EventType, Notify> lastNotifyMap, Map<EventType, List<Event>> eventMap) throws Exception{

        List<Notify> res = new ArrayList<>();
        for (Map.Entry<EventType, List<Event>> e : eventMap.entrySet()) {
            Notify lastNotify = lastNotifyMap != null
                    ? lastNotifyMap.get(e.getKey())
                    : null;
            process(res, lastNotify, e.getValue());
        }
        return res;
    }

    protected List<Notify> process(List<Notify> notifyList, Notify lastNotify, List<Event> eventList) throws Exception{
       
    	
    	if (eventList == null || eventList.size() == 0) return notifyList;
        if (notifyList == null) return null;       

       
        	//System.out.println("Old Notification Details -"+lastNotify);
        
        for (Event event : eventList) {
        Notify notify = null;
            if (lastNotify == null) {
                if (event.getEventSubType() == SOMETHING)
                    notify = Notify.update(new Notify(), event);
            } else if (lastNotify.getEventSubType() != event.getEventSubType()) {
                notify = Notify.update(new Notify(), event);
            }

            if (notify != null) {
            	
                if (lastNotify != null) 
                	Notify.update2(lastNotify, event);
                
               
                
                if(lastNotify!=null)
                {
                	System.out.println("Old Notification FOUND WITH STATUS ---"+lastNotify.getStatus());
                	if(lastNotify.getStatus().equals(NotifyResolveStatusType.SNOOZED))
                	{
                		//SNOOZED NOTIFICATION IS FOUND
                		
                		long tDuration = event.getDate().getTime() - notify.getDate().getTime();
                        if (tDuration > 0) 
                        {
                        long duration = TimeUnit.MILLISECONDS.toMinutes(tDuration);
                        
                        if(duration<lastNotify.getSnoozedurationLong()) {
                        	//Break the loop because its snoozed.. 
                        	continue;
                        }
                        
                        }
                        
                        
                	}
                }
                lastNotify = notify;
                notifyList.add(notify);
            }
        }
        return notifyList;
    }
}

