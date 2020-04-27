package org.joget.geowatch.ght.net.dto.in.resp.log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LogGhtVehicleInResp {

    @SerializedName("vehicle_samples")
    private VehicleSamples vehicleSamples;

    public VehicleSamples getVehicleSamples() {
        return vehicleSamples;
    }

    public void setVehicleSamples(VehicleSamples vehicleSamples) {
        this.vehicleSamples = vehicleSamples;
    }

    public static class VehicleSamples {
        private List<VehicleSample> samples;

        public List<VehicleSample> getSamples() {
            return samples;
        }

        public void setSamples(List<VehicleSample> samples) {
            this.samples = samples;
        }
    }

}


