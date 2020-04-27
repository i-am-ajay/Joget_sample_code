package org.joget.geowatch.ght.net.dto.in.resp.log;

import com.google.gson.annotations.SerializedName;

public class Status {
    private Vehicle vehicle;

    public class Vehicle {
        private Tractor tractor;
        private Trailer trailer;
        @SerializedName("unknown_vehicle")
        private UnknownVehicle unknownVehicle;
    }
}
