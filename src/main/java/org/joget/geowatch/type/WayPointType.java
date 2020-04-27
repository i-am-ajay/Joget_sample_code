package org.joget.geowatch.type;

public enum WayPointType implements ILabel{
    START(""),
    FINISH(""),
    GEOFENCE(""),
    WAY_POINT("");

    private String label;       // fixme: Chenge to labelId for multi language supporting

    WayPointType(String label){
        this.label = label;
    }

    public String label() {
        return label;
    }
}
