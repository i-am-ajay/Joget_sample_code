package org.joget.geowatch.api.dto.out.resp;

import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.Location;
import org.joget.geowatch.db.dto.Notify;
import org.joget.geowatch.db.dto.type.NotifyResolveStatusType;
import org.joget.geowatch.type.EventSubType;
import org.joget.geowatch.type.EventType;
import org.joget.geowatch.type.NotifyType;
import org.joget.geowatch.util.JogetUtil;

import static org.joget.geowatch.util.DateUtil.getUiShortStrDate;

/**
 * Created by k.lebedyantsev
 * Date: 1/25/2018
 * Time: 2:03 PM
 */
public class NotifyOutResp {
    private String id;
    private String tripId;
    private String vehicleId;
    private String ghtVehicleId;
    private String licensePlate;
    private String description;
    private String date;

    private TypeOutResp eventType;
    private TypeOutResp eventSubType;
    private TypeOutResp notificationType;
    private TypeOutResp status;

//    private EventType eventType;
//    private EventSubType eventSubType;
//    private NotifyType notificationType;
//    private NotifyResolveStatusType status;

    private Location location;
    private String note;
    //new data
    private String snoozeduration;
    private String handler;
    private String handleDate;
    private boolean editable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public EventType getEventType() {
        return (EventType) eventType.getOdj();
    }

    public void setEventType(EventType eventType) {
        this.eventType = TypeOutResp.getInstance(eventType);
    }

    public EventSubType getEventSubType() {
        return (EventSubType) eventSubType.getOdj();
    }

    public void setEventSubType(EventSubType eventSubType) {
        this.eventSubType = TypeOutResp.getInstance(eventSubType);
    }

    public NotifyType getNotificationType() {
        return (NotifyType) notificationType.getOdj();
    }

    public void setNotificationType(NotifyType notificationType) {
        this.notificationType = TypeOutResp.getInstance(notificationType);
    }

    public NotifyResolveStatusType getStatus() {
        return (NotifyResolveStatusType) status.getOdj();
    }

    public void setStatus(NotifyResolveStatusType status) {
        this.status = TypeOutResp.getInstance(status);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(String handleDate) {
        this.handleDate = handleDate;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    //new code 
    
    public String getSnoozeduration() {
  		return snoozeduration;
  	}

  	public void setSnoozeduration(String snoozeduration) {
  		this.snoozeduration = snoozeduration;
  	}

    public static NotifyOutResp[] update(NotifyOutResp[] items, Notify[] notifies, User user) throws Exception {
        if (items == null) return items;
        if (notifies == null) return items;

        for (int i = 0; i < items.length; i++)
            items[i] = update(
                    items[i] != null ? items[i] : new NotifyOutResp(),
                    notifies[i], user);

        return items;
    }

  

	public static NotifyOutResp update(NotifyOutResp item, Notify notify, User user) throws Exception {
        if (item == null) return item;
        if (notify == null) return item;
        if (notify.getEvent1() == null) return item;

        item.editable = JogetUtil.isEditable(user);
        item.id = notify.getId();
        item.tripId = notify.getTripId();
        item.vehicleId = notify.getVehicleId();
        item.ghtVehicleId = notify.getGhtVehicleId();
        item.licensePlate = notify.getGhtVehicleId();
        item.description = notify.getDescription();
        item.note = notify.getNote();
        item.handler = notify.getHandler();
        
        //new code
        
        item.snoozeduration =notify.getSnoozeduration();

        item.date = getUiShortStrDate(notify.getDate(), user);
        item.handleDate = getUiShortStrDate(notify.getHandleDate(), user);

        item.location = Location.update1(new Location(), notify);

        item.setNotificationType(notify.getNotifyType());
        item.setStatus(notify.getStatus());
        item.setEventType(notify.getEventType());
        item.setEventSubType(notify.getEventSubType());

        return item;
    }
}
