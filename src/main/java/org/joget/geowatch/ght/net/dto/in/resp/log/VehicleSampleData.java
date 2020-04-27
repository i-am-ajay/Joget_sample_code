package org.joget.geowatch.ght.net.dto.in.resp.log;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:08 PM
 */
public class VehicleSampleData {
    private Tractor tractor;
    private Trailer trailer;
    private UnknownVehicle unknown_vehicle;

    public Tractor getTractor() {
        return tractor;
    }

    public void setTractor(Tractor tractor) {
        this.tractor = tractor;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public void setTrailer(Trailer trailer) {
        this.trailer = trailer;
    }

    public UnknownVehicle getUnknown_vehicle() {
        return unknown_vehicle;
    }

    public void setUnknown_vehicle(UnknownVehicle unknown_vehicle) {
        this.unknown_vehicle = unknown_vehicle;
    }
}
