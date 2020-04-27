package org.joget.geowatch.ght.net.dto.in.resp.log.simple;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:48 PM
 */
public class Ebs {
    private String timestamp;
    private Integer odometer;
    private Double speed;
    private Boolean coupled;//Coupled state of the vehicle
    private Boolean ignition;//Ignition state of the vehicle
    private Integer load;//Axle load of the vehicle [kg]
    private Boolean brake_lining_ok;//Status of the brake pads

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getOdometer() {
        return odometer;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Boolean getCoupled() {
        return coupled;
    }

    public void setCoupled(Boolean coupled) {
        this.coupled = coupled;
    }

    public Boolean getIgnition() {
        return ignition;
    }

    public void setIgnition(Boolean ignition) {
        this.ignition = ignition;
    }

    public Integer getLoad() {
        return load;
    }

    public void setLoad(Integer load) {
        this.load = load;
    }

    public Boolean getBrake_lining_ok() {
        return brake_lining_ok;
    }

    public void setBrake_lining_ok(Boolean brake_lining_ok) {
        this.brake_lining_ok = brake_lining_ok;
    }
}
