package org.joget.geowatch.db.dto.inner;

import org.joget.geowatch.db.dto.type.VehicleType;
import org.joget.geowatch.db.dto.type.YesNoType;

public class VehicleInnerEntity {

    protected String id;
    protected String name;
    protected String requesterOrganizationId;
    protected String requesterDepartmentId;
    protected VehicleType type;
    protected YesNoType panicButton;
    protected YesNoType doorAlarm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequesterOrganizationId() {
        return requesterOrganizationId;
    }

    public void setRequesterOrganizationId(String requesterOrganizationId) {
        this.requesterOrganizationId = requesterOrganizationId;
    }

    public String getRequesterDepartmentId() {
        return requesterDepartmentId;
    }

    public void setRequesterDepartmentId(String requesterDepartmentId) {
        this.requesterDepartmentId = requesterDepartmentId;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public YesNoType getPanicButton() {
        return panicButton;
    }

    public void setPanicButton(YesNoType panicButton) {
        this.panicButton = panicButton;
    }

    public YesNoType getDoorAlarm() {
        return doorAlarm;
    }

    public void setDoorAlarm(YesNoType doorAlarm) {
        this.doorAlarm = doorAlarm;
    }
}
