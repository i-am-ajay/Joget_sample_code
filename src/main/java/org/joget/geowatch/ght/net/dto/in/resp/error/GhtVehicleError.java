package org.joget.geowatch.ght.net.dto.in.resp.error;

/**
 * Created by k.lebedyantsev
 * Date: 2/14/2018
 * Time: 2:04 PM
 */
public class GhtVehicleError {
    private VehicleErrorCode code;
    private String message;

    public VehicleErrorCode getCode() {
        return code;
    }

    public void setCode(VehicleErrorCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
