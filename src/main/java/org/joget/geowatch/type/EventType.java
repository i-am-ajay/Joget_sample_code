package org.joget.geowatch.type;

/**
 * Created by k.lebedyantsev
 * Date: 2/21/2018
 * Time: 11:52 AM
 */
public enum EventType implements ILabel{
    GHT_NET("GHT issue"),
    DOOR1("Door-1 issue"),
    DOOR2("Door-2 issue"),
    DOOR3("Door-3 issue"),
    DOOR4("Door-4 issue"),
    ROUTE("Route issue"),
    GEOFENCE("Geofence issue"),
    GEOFENCE_START("Start geofence issue"),
    GEOFENCE_FINISH("Finish geofence issue"),
    DELAY_START("Delay start issue"),
    DELAY_FINISH("Delay finish issue"),
    POD_SUBMIT("Pod issue"),
    NO_DATA("No Data issue"),
    STOPPED_UNKNOWN_LOCATION("STOPPED UNKNOWN LOCATION"),
	STOPPED_BLACKLIST_LOCATION("STOPPED BLACKLIST LOCATION"),
	STOPPED_REDZONE_LOCATION("STOPPED REDZONE LOCATION"),
	DELAY_START_NEW("Device is 60 mins late"),
	DOOR_OPEN("DOOR OPEN");

    private String label;       // fixme: Chenge to labelId for multi language supporting

    EventType(String label){
        this.label = label;
    }

    public String label() {
        return label;
    }
}

