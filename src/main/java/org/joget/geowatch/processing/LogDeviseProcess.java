package org.joget.geowatch.processing;

import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.ExtDirectoryManager;
import org.joget.geowatch.app.AppContext;
import org.joget.geowatch.app.GglGeoWatchPluginActivator;
import org.joget.geowatch.db.dto.Event;
import org.joget.geowatch.db.dto.Log;
import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;
import org.joget.geowatch.db.dto.inner.VehicleLastPositionInnerEntity;
import org.joget.geowatch.db.service.EventService;
import org.joget.geowatch.db.service.GeofenceService;
import org.joget.geowatch.db.service.LogService;
import org.joget.geowatch.db.service.NotifyService;
import org.joget.geowatch.db.service.TripService;
import org.joget.geowatch.ght.processing.GhtLogProcessing;
import org.joget.geowatch.processing.analize.Analyzer;
import org.joget.geowatch.processing.dto.EventData;
import org.joget.geowatch.processing.dto.LogData;
import org.joget.geowatch.processing.dto.NotifyData;
import org.joget.geowatch.processing.dto.VehicleProcessData;
import org.joget.geowatch.type.EventSubType;
import org.joget.geowatch.type.EventType;
import org.joget.geowatch.type.NotifyType;
import org.joget.geowatch.util.JogetUtil;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNoneEmpty;
import static org.joget.geowatch.app.AppProperties.*;
import static org.joget.geowatch.db.dto.type.VehicleType.HAULIER;
import static org.joget.geowatch.db.dto.type.VehicleType.RMO;
import static org.joget.geowatch.db.dto.type.VehicleType.TRAILER;
import static org.joget.geowatch.type.EventSubType.NONSENSE;
import static org.joget.geowatch.type.EventSubType.SOMETHING;
import static org.joget.geowatch.type.EventSubType.UNKNOWN;
import static org.joget.geowatch.util.DateUtil.getUiShortStrDate;


public class LogDeviseProcess {
    private static final String TAG = LogDeviseProcess.class.getSimpleName();

    private TripService tripService;
    private LogService logService;
    private GhtLogProcessing ghtLogProcessing;
    private EventProcess eventProcess;
    private EventService eventService;
    private NotifyProcess notifyProcess;
    private NotifyService notifyService;
    private PodProcess podProcess;
    private GeofenceService geofenceService;

    public LogDeviseProcess(
            TripService tripService, LogService logService,
            GhtLogProcessing ghtLogProcessing, EventProcess eventProcess,
            EventService eventService, NotifyProcess notifyProcess,
            NotifyService notifyService, PodProcess podProcess,GeofenceService geofenceService) {
        this.tripService = tripService;
        this.logService = logService;
        this.ghtLogProcessing = ghtLogProcessing;
        this.eventProcess = eventProcess;
        this.eventService = eventService;
        this.notifyProcess = notifyProcess;
        this.notifyService = notifyService;
        this.podProcess = podProcess;
        this.geofenceService=geofenceService;
    }

    public void process() throws Exception {
        List<Trip> liveTripList = tripService.getLiveTrip();
        if (PLUGIN_DEBUG_MODE) {
            LogUtil.info(TAG, "LogDeviseProcess start process LIVE trips count: " + liveTripList.size());
        }
        if (liveTripList != null) for (Trip trip : liveTripList) {
            try {
                if (PLUGIN_DEBUG_MODE) {
                    LogUtil.info(TAG, "LogDeviseProcess start process LIVE trip: " + trip.getC__id());
                }
                Trip tripRefreshed = tripService.getTrip(trip.getId());
                process(tripRefreshed);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "ERROR.");
            }
        }
    }

    protected void process(Trip trip) throws Exception {
        LogUtil.info(TAG, "PROCESS LOG 4 TRIP. Id: " + trip.getId());
        
        
        //New mwthod for snooze alert
       

        VehicleProcessData vehicleProcessData = ghtLogProcessing.process(trip);
        Analyzer.analyze(vehicleProcessData,geofenceService);
        saveLog(vehicleProcessData);
        updateTripLastPosition(vehicleProcessData, trip);

        eventProcess.process(vehicleProcessData);
        saveEvent(vehicleProcessData);

        podProcess.process(vehicleProcessData);

        notifyProcess.process(vehicleProcessData);
        saveAndUpdateNotify(vehicleProcessData);
        try {
        checkSnoozeAlert(trip);
        }
        catch (Exception e) {
			// TODO: handle exception
        	
        	LogUtil.info(TAG, "Error in the catch snooze method");
        	e.printStackTrace();
		}
    }

    protected void saveLog(VehicleProcessData vehicleProcessData) throws Exception {
        saveLog(vehicleProcessData.getNewLogData(HAULIER));
        saveLog(vehicleProcessData.getNewLogData(TRAILER));
        saveLog(vehicleProcessData.getNewLogData(RMO));
    }

    protected void saveLog(LogData logData) throws Exception {
        if (logData != null && logData.getLogList() != null)
            {
        		logService.save(logData.getLogList());
            }
            
    }
    
    
    //New Method for Snooze Alert Check
    protected void checkSnoozeAlert(Trip trip) throws Exception {
    	
    	//Check code in package "org.joget.geowatch.db.service.impl" for below impl 
    	//NotifyServiceImpl
    	//LogServiceImpl
    	List<Notify> snoozedTrips=notifyService.listSnoozed(trip.getId());
    	LogUtil.info(LogDeviseProcess.TAG, "Total Snoozed ----"+snoozedTrips.size());
    	for (Iterator iterator = snoozedTrips.iterator(); iterator.hasNext();) {
			Notify notify = (Notify) iterator.next();
			LogUtil.info(LogDeviseProcess.TAG, "Snoozed notification "+notify.getId());
			Notify oldnotify=notifyService.getLastNotification(notify.getTripId(), notify.getEventType(), notify.getGhtVehicleId());
			if(oldnotify!=null)
			{
				if(oldnotify.getId().equals(notify.getId())==false)
				{
					//New notification is already added for the same device so no action is needed .
					LogUtil.info(LogDeviseProcess.TAG, "Notification is already generated");
					notify.setRead_status("1");
					continue;
				}
			}
			
			Log log=logService.getLastLogDetails(notify.getTripId(), notify.getVehicleId());
			
			LogUtil.info(LogDeviseProcess.TAG, "Recent logs are "+log);
			//code to process the notification by analyzer
			if(log!=null)
			{
				Analyzer.analyze(trip, notify.getEventType(), geofenceService, log);
			}else
			{
				//as there is no active logs and no information 
				Notify newAlert=notify.copy();
				LogUtil.info(LogDeviseProcess.TAG, "Recent logs are null analyzer is not called..");
				notifyService.save(newAlert);
			}
			
			
			notify.setRead_status("1");
		}
    	
    	notifyService.update(snoozedTrips);
    
        
    }

    private void updateTripLastPosition(VehicleProcessData vehicleProcessData, Trip trip) throws Exception {
        LogData logData;

        logData = vehicleProcessData.getNewLogData(HAULIER);
       
        if (logData != null && logData.getLogList() != null && logData.getLogList().size() > 0) {
        	 LogUtil.info(LogDeviseProcess.TAG,"Logs Size HAULIER"+logData.getLogList().size());
            VehicleLastPositionInnerEntity haulierLastPosition = getLastPosition(logData.getLogList(), trip.getHaulierLastPosition());
            trip.setHaulierLastPosition(haulierLastPosition);
        }

        logData = vehicleProcessData.getNewLogData(TRAILER);
        if (logData != null && logData.getLogList() != null && logData.getLogList().size() > 0) {
        	 LogUtil.info(LogDeviseProcess.TAG,"Logs Size TRAILER"+logData.getLogList().size());
            VehicleLastPositionInnerEntity haulierTrailerLastPosition = getLastPosition(logData.getLogList(), trip.getHaulierTrailerLastPosition());
            trip.setHaulierTrailerLastPosition(haulierTrailerLastPosition);
        }

        logData = vehicleProcessData.getNewLogData(RMO);
        if (logData != null && logData.getLogList() != null && logData.getLogList().size() > 0) {
        	LogUtil.info(LogDeviseProcess.TAG,"Logs Size RMO"+logData.getLogList().size());
            VehicleLastPositionInnerEntity rmoLastPosition = getLastPosition(logData.getLogList(), trip.getRmoLastPosition());
            trip.setRmoLastPosition(rmoLastPosition);
        }

        tripService.update(trip);
    }

    private VehicleLastPositionInnerEntity getLastPosition(List<Log> logList, VehicleLastPositionInnerEntity dbLastPositionData) {
        for (int i = logList.size() - 1; i >= 0; i--) {
            Log log = logList.get(i);
            if (!isBlank(log.getLat()) && !isBlank(log.getLng())) {
                if (dbLastPositionData != null && dbLastPositionData.getDate() != null) {
                    if (log.getDate() != null && log.getDate().after(dbLastPositionData.getDate())) {
                        dbLastPositionData = VehicleLastPositionInnerEntity.update(dbLastPositionData, log);
                        break;
                    }
                } else {
                    dbLastPositionData = VehicleLastPositionInnerEntity.create(log);
                    break;
                }
            }
        }
        return dbLastPositionData;
    }

    protected void saveEvent(VehicleProcessData vehicleProcessData) throws Exception {
        saveEvent(vehicleProcessData.getNewEventData(HAULIER));
        saveEvent(vehicleProcessData.getNewEventData(TRAILER));
        saveEvent(vehicleProcessData.getNewEventData(RMO));
    }

    protected void saveEvent(EventData eventData) throws Exception {
        if (eventData != null && eventData.getNewEventMap() != null)
            for (List<Event> l : eventData.getNewEventMap().values())
                eventService.save(l);
    }

    protected void saveAndUpdateNotify(VehicleProcessData vehicleProcessData) throws Exception {
        saveAndUpdateNotify(vehicleProcessData.getNewNotifyData(HAULIER));
        saveAndUpdateNotify(vehicleProcessData.getNewNotifyData(TRAILER));
        saveAndUpdateNotify(vehicleProcessData.getNewNotifyData(RMO));
    }

    protected void saveAndUpdateNotify(NotifyData notifyData) throws Exception {
        if (notifyData != null && notifyData.getNotifyList() != null && notifyData.getNotifyList().size() > 0) {
            notifyService.save(notifyData.getNotifyList());
            sendEmail(notifyData.getNotifyList());
            if (notifyData.getLastNotifyEventMap() != null)
                notifyService.update(notifyData.getLastNotifyEventMap().values());
        }
    }

    protected void sendEmail(List<Notify> notifyList) throws Exception {
        if (notifyList != null) for (Notify notify : notifyList) {
            try {
                sendEmail(notify);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "Error.");
            }
        }
    }

    protected void sendEmail(Notify notify) throws Exception {

        Set<String> userIdList = new HashSet<>();

        String[] userIdArr;
        
/*        userIdArr = !isEmpty(notify.getTrip().getEmailRequesterGroup())
                ? notify.getTrip().getEmailRequesterGroup().split(";") : null;
        if (userIdArr != null) userIdList.addAll(Arrays.asList(userIdArr));

        userIdArr = !isEmpty(notify.getTrip().getEmailHaulierGroup())
                ? notify.getTrip().getEmailHaulierGroup().split(";") : null;
        if (userIdArr != null) userIdList.addAll(Arrays.asList(userIdArr));

        userIdArr = !isEmpty(notify.getTrip().getEmailRmoGroup())
                ? notify.getTrip().getEmailRmoGroup().split(";") : null;
        if (userIdArr != null) userIdList.addAll(Arrays.asList(userIdArr));
        
*/
    
        //Sent Email only to Monitor user
        
        userIdArr = !isEmpty(notify.getTrip().getEmailMonitorGroup())
                ? notify.getTrip().getEmailMonitorGroup().split(";") : null;
        if (userIdArr != null) userIdList.addAll(Arrays.asList(userIdArr));

        for (String userId : userIdList) {
            String bodyKey = null;
            try {
                bodyKey = getBody(notify.getEventType(), notify.getEventSubType(), notify.getNotifyType());
                
                System.out.println("--------------In Email Method for loop--------------");
                System.out.println("Body Key :"+bodyKey);
                
                if (bodyKey != null) sendEmail(userId, notify.getTrip(), notify.getGhtVehicle(), notify, bodyKey);
            } catch (Exception e) {
                LogUtil.error(TAG, e, "Error. Send email. tripId: " + notify.getTrip().getId() + ", userId: " + userId + ", bodyKey: " + bodyKey);
            }
        }
    }

    public static String getBody(EventType eventType, EventSubType eventSubType, NotifyType notifyType) {
        if (UNKNOWN == eventSubType) return null;
        
        System.out.println("--------------In Get Body--------------");
        System.out.println("Event Type :"+eventType);
        System.out.println("Event Sub Type :"+eventSubType);

        String key = null;
        switch (eventType) {
            case DOOR1:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_OPEN_DOOR;
                else if (NONSENSE == eventSubType) key = null;
                break;
            case DOOR2:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_OPEN_DOOR;
                else if (NONSENSE == eventSubType) key = null;
                break;
            case DOOR3:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_OPEN_DOOR;
                else if (NONSENSE == eventSubType) key = null;
                break;
            case DOOR4:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_OPEN_DOOR;
                else if (NONSENSE == eventSubType) key = null;
                break;
            case POD_SUBMIT:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_NEED_SUBMIT_POD;
                else if (NONSENSE == eventSubType) key = null;
                break;
            case ROUTE:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_LEFT_THE_ROUTE;
                else if (NONSENSE == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_RETURNED_TO_ROUTE;
                break;
            case GEOFENCE:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_ZONE;
                else if (NONSENSE == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_ZONE;
                break;
            case GEOFENCE_START:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_START_ZONE;
                else if (NONSENSE == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_START_ZONE;
                break;
            case GEOFENCE_FINISH:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_ENTERED_TO_FINISH_ZONE;
                else if (NONSENSE == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_LEAVE_FROM_FINISH_ZONE;
                break;
            case GHT_NET:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_GHTRACK;
                else if (NONSENSE == eventSubType) key = null;
                break;
            case DELAY_START:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_DELAY_START_ZONE;
                else if (NONSENSE == eventSubType) key = null;
                break;
            case DELAY_FINISH:
                if (SOMETHING == eventSubType) key = KEY_EMAIL_BODY_NOTIFICATION_DELAY_FINISH_ZONE;
                else if (NONSENSE == eventSubType) key = null;
                break;
                
              //New code
                
			case NO_DATA:
				if (SOMETHING == eventSubType) {
					
					System.out.println("In No data Alert");
					key = KEY_EMAIL_BODY_NOTIFICATION_NO_DATA;
				}
				else if (NONSENSE == eventSubType)
					key = null;
				break;
			case STOPPED_UNKNOWN_LOCATION:
				if (SOMETHING == eventSubType)
					key = KEY_EMAIL_BODY_NOTIFICATION_STOPPED_UNKNOWN_LOCATION;
				else if (NONSENSE == eventSubType)
					key = null;
				break;
	
			case STOPPED_BLACKLIST_LOCATION:
				if (SOMETHING == eventSubType)
					key = KEY_EMAIL_BODY_NOTIFICATION_STOPPED_BLACKLIST_LOCATION;
				else if (NONSENSE == eventSubType)
					key = null;
				break;
	
			case STOPPED_REDZONE_LOCATION:
				if (SOMETHING == eventSubType)
					key = KEY_EMAIL_BODY_NOTIFICATION_STOPPED_REDZONE_LOCATION;
				else if (NONSENSE == eventSubType)
					key = null;
				break;
	
			case DELAY_START_NEW:
				if (SOMETHING == eventSubType)
					key = KEY_EMAIL_BODY_NOTIFICATION_DELAY_START_NEW;
				else if (NONSENSE == eventSubType)
					key = null;
				break;
	 
            default:
                key = null;
                break;
        }
        return key;
    }

    public static void sendEmail(String userId, Trip trip, GhtVehicleInnerEntity ghtVehicle, Notify notify, String bodyKey) throws Exception {
        ExtDirectoryManager directoryManager = AppContext.getBean("directoryManager", ExtDirectoryManager.class);
        User user = directoryManager.getUserById(userId);
        if (user == null) return;
        
       System.out.println("---------In send Email method------------");
      
    

        String eventDateStr = getUiShortStrDate(notify.getDate(), user);
        String body = ResourceBundleUtil.getMessage(bodyKey, new Locale(JOGET_SYSTEM_LOCALE));
        if (!isEmpty(user.getLocale()))
            body = ResourceBundleUtil.getMessage(bodyKey, body, new Locale(user.getLocale()));
        if (isEmpty(body)) return;
        
       // System.out.println("Body :"+body);
      
    	 

        JogetUtil.Email email = new JogetUtil.Email(
                userId,
                trip.getC__id(),
                trip.getRequesterDepartmentId(),
                ghtVehicle != null ? ghtVehicle.getId() : "",
                eventDateStr,
                isNoneEmpty(notify.getDescription()) ? notify.getDescription() : "",
                body,
                (notify.getEvent1() != null && notify.getEvent1().getWp() != null)
                        ? notify.getEvent1().getWp().getName()
                        : ""
        );
        
       

        GglGeoWatchPluginActivator.sendEmail(email);
    }
}
