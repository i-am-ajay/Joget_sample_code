package org.joget.geowatch.ght.net.dto.in.resp.log;

import org.joget.geowatch.ght.net.dto.in.resp.log.simple.*;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:19 PM
 */
public class VehicleBase {
    private Id id;
    private String haulier;//VehicleSampleGroup haulier
    private String tracked_always_group;//Group name, only used if the vehicle is tracked always
    private String timestamp;//UTC time when data was sampled in device, i.e. device time, format ISO-8601
    private String server_timestamp;//UTC time when data was received by the server, i.e. server time, format ISO-8601
    private Device device;
    private Driver driver;
    private Position position;
    private Power power;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public String getHaulier() {
        return haulier;
    }

    public void setHaulier(String haulier) {
        this.haulier = haulier;
    }

    public String getTracked_always_group() {
        return tracked_always_group;
    }

    public void setTracked_always_group(String tracked_always_group) {
        this.tracked_always_group = tracked_always_group;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getServer_timestamp() {
        return server_timestamp;
    }

    public void setServer_timestamp(String server_timestamp) {
        this.server_timestamp = server_timestamp;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Power getPower() {
        return power;
    }

    public void setPower(Power power) {
        this.power = power;
    }
}
