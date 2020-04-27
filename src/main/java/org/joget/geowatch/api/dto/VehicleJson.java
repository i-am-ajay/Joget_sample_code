package org.joget.geowatch.api.dto;

import org.joget.geowatch.db.dto.type.VehicleType;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/17/2018
 * Time: 2:21 PM
 */
public class VehicleJson {
    private String licensePlate;
    private String vehicleId;
    private String ghtVehicleId;
    private VehicleType type;
    private Location currentPosition;
    private List<Location> allLocations;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Location getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Location currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<Location> getAllLocations() {
        return allLocations;
    }

    public void setAllLocations(List<Location> allLocations) {
        this.allLocations = allLocations;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }
}
