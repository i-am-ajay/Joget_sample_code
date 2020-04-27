package org.joget.geowatch.ght.net.dto.in.resp.log;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:10 PM
 */
public class Tractor {
    private Id trailer_1_license_plate;
    private Id trailer_2_license_plate;
    private VehicleBase vehicle_base;

    public Id getTrailer_1_license_plate() {
        return trailer_1_license_plate;
    }

    public void setTrailer_1_license_plate(Id trailer_1_license_plate) {
        this.trailer_1_license_plate = trailer_1_license_plate;
    }

    public Id getTrailer_2_license_plate() {
        return trailer_2_license_plate;
    }

    public void setTrailer_2_license_plate(Id trailer_2_license_plate) {
        this.trailer_2_license_plate = trailer_2_license_plate;
    }

    public VehicleBase getVehicle_base() {
        return vehicle_base;
    }

    public void setVehicle_base(VehicleBase vehicle_base) {
        this.vehicle_base = vehicle_base;
    }
}
