package org.joget.geowatch.db.dto.type;

import org.joget.geowatch.type.ILabel;
import org.joget.geowatch.type.IValue;

public enum JobType implements IValue, ILabel {

    TSR_1_VAN("TSR 1 - Van", "TSR 1 - Van"),
    TSR_1_COMBO("TSR 1 - Combo", "TSR 1 - Combo"),
    HTSP_VAN_FCL("HTSP - Van/FCL", "HTSP - Van/FCL"),
    HTSP_COMBO("HTSP - Combo", "HTSP - Combo"),
    GROUPAGE("Groupage", "Groupage"),
    CONTAINER("Container", "Container"),
    SELF_HAULING("Self-Hauling", "Self-Hauling"),
    VISIBILITY_ONLY("Visibility Only", "Visibility Only"),
	CUSTOM_JOB("Custom Job","Custom Job");

    protected String value;
    protected String label;

    JobType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String label() {
        return label;
    }
}
