package org.joget.geowatch.ght.net.dto.in.resp.log.simple;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:41 PM
 */
public class TirePressure {
    private String timestamp;//UTC time when the tire pressures was sampled in the device, i.e. device time, format ISO-8601
    private Double front_left;//Pressure front left [bar]
    private Double front_right;//Pressure front right[bar]
    private Double middle_left;//Pressure middle left [bar]
    private Double middle_right;//Pressure middle right[bar]
    private Double rear_left;//Pressure rear left [bar]
    private Double rear_right;//Pressure rear right [bar]
    private Double spare;//Pressure spare [bar]

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getFront_left() {
        return front_left;
    }

    public void setFront_left(Double front_left) {
        this.front_left = front_left;
    }

    public Double getFront_right() {
        return front_right;
    }

    public void setFront_right(Double front_right) {
        this.front_right = front_right;
    }

    public Double getMiddle_left() {
        return middle_left;
    }

    public void setMiddle_left(Double middle_left) {
        this.middle_left = middle_left;
    }

    public Double getMiddle_right() {
        return middle_right;
    }

    public void setMiddle_right(Double middle_right) {
        this.middle_right = middle_right;
    }

    public Double getRear_left() {
        return rear_left;
    }

    public void setRear_left(Double rear_left) {
        this.rear_left = rear_left;
    }

    public Double getRear_right() {
        return rear_right;
    }

    public void setRear_right(Double rear_right) {
        this.rear_right = rear_right;
    }

    public Double getSpare() {
        return spare;
    }

    public void setSpare(Double spare) {
        this.spare = spare;
    }
}
