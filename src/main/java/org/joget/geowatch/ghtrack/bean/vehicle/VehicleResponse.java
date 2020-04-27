package org.joget.geowatch.ghtrack.bean.vehicle;

import com.google.gson.annotations.SerializedName;
import org.joget.geowatch.ght.net.dto.in.resp.error.GhtVehicleError;
import org.joget.geowatch.ght.net.dto.in.resp.log.VehicleSample;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 2/15/2018
 * Time: 11:02 AM
 */
public class VehicleResponse {
    private String tripId;
    private String vehicleId;
    private String ghtVehicleId;
    private VehicleResult result;
    private GhtVehicleError error;

    public VehicleResponse(String tripId, String vehicleId, String ghtVehicleId, VehicleResult result, GhtVehicleError error) {
        this.tripId = tripId;
        this.vehicleId = vehicleId;
        this.ghtVehicleId = ghtVehicleId;
        this.result = result;
        this.error = error;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }

    public VehicleResult getResult() {
        return result;
    }

    public void setResult(VehicleResult result) {
        this.result = result;
    }

    public GhtVehicleError getError() {
        return error;
    }

    public void setError(GhtVehicleError error) {
        this.error = error;
    }

    public static class VehicleResult {

        @SerializedName("vehicle_samples")
        private VehicleSamples vehicleSamples;

        public VehicleSamples getVehicleSamples() {
            return vehicleSamples;
        }

        public void setVehicleSamples(VehicleSamples vehicleSamples) {
            this.vehicleSamples = vehicleSamples;
        }
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
