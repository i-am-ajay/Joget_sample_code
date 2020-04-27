package org.joget.geowatch.type;

/**
 * Created by k.lebedyantsev
 * Date: 1/22/2018
 * Time: 10:54 AM
 */
public enum NotifyType implements ILabel{
    NOTHING("Nothing"),
    NOTICE("Notice"),
    ALERT("Alert");

    private String label;       // fixme: Chenge to labelId for multi language supporting

    NotifyType(String label){
        this.label = label;
    }

    public String label() {
        return label;
    }
}
