package org.joget.geowatch.ght.net.dto.in.resp.log.simple;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:32 PM
 */
public class PositionQuality {
    private Integer satelites_used;//Number of GPS satellites used for position fix
    private Double hdop;//The current HDOP value
    private Double accuracy;//The estimated accuracy of this position

    public Integer getSatelites_used() {
        return satelites_used;
    }

    public void setSatelites_used(Integer satelites_used) {
        this.satelites_used = satelites_used;
    }

    public Double getHdop() {
        return hdop;
    }

    public void setHdop(Double hdop) {
        this.hdop = hdop;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }
}
