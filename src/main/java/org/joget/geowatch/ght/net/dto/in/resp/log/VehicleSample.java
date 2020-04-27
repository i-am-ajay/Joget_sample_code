package org.joget.geowatch.ght.net.dto.in.resp.log;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:06 PM
 */
public class VehicleSample {
    private VehicleSampleGroup status;
    private VehicleSampleGroup motion;
    private VehicleSampleGroup ignition;
    private VehicleSampleGroup coupling;
    private VehicleSampleGroup reefer_alarm;
    private VehicleSampleGroup door_state_changed;
    private VehicleSampleGroup brake_lining_alarm;

    public VehicleSampleGroup getStatus() {
        return status;
    }

    public void setStatus(VehicleSampleGroup status) {
        this.status = status;
    }

    public VehicleSampleGroup getMotion() {
        return motion;
    }

    public void setMotion(VehicleSampleGroup motion) {
        this.motion = motion;
    }

    public VehicleSampleGroup getIgnition() {
        return ignition;
    }

    public void setIgnition(VehicleSampleGroup ignition) {
        this.ignition = ignition;
    }

    public VehicleSampleGroup getCoupling() {
        return coupling;
    }

    public void setCoupling(VehicleSampleGroup coupling) {
        this.coupling = coupling;
    }

    public VehicleSampleGroup getReefer_alarm() {
        return reefer_alarm;
    }

    public void setReefer_alarm(VehicleSampleGroup reefer_alarm) {
        this.reefer_alarm = reefer_alarm;
    }

    public VehicleSampleGroup getDoor_state_changed() {
        return door_state_changed;
    }

    public void setDoor_state_changed(VehicleSampleGroup door_state_changed) {
        this.door_state_changed = door_state_changed;
    }

    public VehicleSampleGroup getBrake_lining_alarm() {
        return brake_lining_alarm;
    }

    public void setBrake_lining_alarm(VehicleSampleGroup brake_lining_alarm) {
        this.brake_lining_alarm = brake_lining_alarm;
    }
}
