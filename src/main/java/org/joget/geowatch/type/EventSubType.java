package org.joget.geowatch.type;

public enum EventSubType implements ILabel{
    NONSENSE("nonsense"),
    SOMETHING("something"),
    UNKNOWN("unknown");

    public EventSubType getOpposite() {
        if(this == NONSENSE)return SOMETHING;
        if(this == SOMETHING)return NONSENSE;
        else return null;
    }

    private String label;       // fixme: Chenge to labelId for multi language supporting

    EventSubType(String label){
        this.label = label;
    }

    public String label() {
        return label;
    }
}
