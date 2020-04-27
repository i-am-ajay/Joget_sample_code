package org.joget.geowatch.ght.net.dto.in.resp.error;

/**
 * Created by k.lebedyantsev
 * Date: 1/8/2018
 * Time: 5:44 PM
 */
public class TourError {
    private TourErrorCode code;
    private String message;

    public TourErrorCode getCode() {
        return code;
    }

    public void setCode(TourErrorCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
