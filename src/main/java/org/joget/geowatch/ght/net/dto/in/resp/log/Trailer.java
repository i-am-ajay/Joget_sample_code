package org.joget.geowatch.ght.net.dto.in.resp.log;

import org.joget.geowatch.ght.net.dto.in.resp.log.simple.*;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:10 PM
 */
public class Trailer {
    private Id tractor_license_plate;
    private VehicleBase vehicle_base;
    private TirePressure tire_pressure;
    private Doors doors;
    private Temperature temperatures;
    private Ebs ebs;

    public Id getTractor_license_plate() {
        return tractor_license_plate;
    }

    public void setTractor_license_plate(Id tractor_license_plate) {
        this.tractor_license_plate = tractor_license_plate;
    }

    public VehicleBase getVehicle_base() {
        return vehicle_base;
    }

    public void setVehicle_base(VehicleBase vehicle_base) {
        this.vehicle_base = vehicle_base;
    }

    public TirePressure getTrailer_tire_pressure() {
        return tire_pressure;
    }

    public void setTrailer_tire_pressure(TirePressure trailer_tire_pressure) {
        this.tire_pressure = trailer_tire_pressure;
    }

    public Ebs getEbs() {
        return ebs;
    }

    public void setEbs(Ebs ebs) {
        this.ebs = ebs;
    }

    public Doors getDoors() {
        return doors;
    }

    public void setDoors(Doors doors) {
        this.doors = doors;
    }

    public TirePressure getTire_pressure() {
        return tire_pressure;
    }

    public void setTire_pressure(TirePressure tire_pressure) {
        this.tire_pressure = tire_pressure;
    }

    public Temperature getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(Temperature temperatures) {
        this.temperatures = temperatures;
    }
}
