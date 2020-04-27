package org.joget.geowatch.ght.net.dto.in.resp.log;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:10 PM
 */
public class UnknownVehicle {
    private VehicleSampleGroup license_plate;

    public VehicleSampleGroup getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(VehicleSampleGroup license_plate) {
        this.license_plate = license_plate;
    }
}
