package org.joget.geowatch.ght.net.dto.in.resp.log;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:14 PM
 */
public class Id {
    private String id;//Unique GateHouse id for the vehicle
    private String name;//Some vehicles may have an additional logical name
    private String license_plate;//VehicleSampleGroup license plate
    private String chassis_number;//VehicleSampleGroup chassis number
    private Boolean operational;

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

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public String getChassis_number() {
        return chassis_number;
    }

    public void setChassis_number(String chassis_number) {
        this.chassis_number = chassis_number;
    }

    public Boolean getOperational() {
        return operational;
    }

    public void setOperational(Boolean operational) {
        this.operational = operational;
    }
}