package org.joget.geowatch.ght.net.dto.in.resp.log.simple;

import org.joget.geowatch.ght.net.dto.in.resp.log.Address;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:26 PM
 */
public class Position {
    private Boolean in_motion;//Motion state of the vehicle
    private Integer accumulated_driving_time;//Accumulated driving time [s]
    private Gps gps;
    private Address address;

    public Boolean getIn_motion() {
        return in_motion;
    }

    public void setIn_motion(Boolean in_motion) {
        this.in_motion = in_motion;
    }

    public Integer getAccumulated_driving_time() {
        return accumulated_driving_time;
    }

    public void setAccumulated_driving_time(Integer accumulated_driving_time) {
        this.accumulated_driving_time = accumulated_driving_time;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
