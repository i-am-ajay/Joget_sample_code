package org.joget.geowatch.db.dto.type;

import org.joget.geowatch.type.ILabel;

/**
 * Created by k.lebedyantsev
 * Date: 2/24/2018
 * Time: 9:58 AM
 */
public enum NotifyResolveStatusType implements ILabel {
    NEW("New"),
    CLOSED("Closed");

    private String label;       // fixme: Chenge to labelId for multi language supporting

    NotifyResolveStatusType(String label){
        this.label = label;
    }

    public String label() {
        return label;
    }
}
