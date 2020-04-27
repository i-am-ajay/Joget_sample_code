package org.joget.geowatch.ght.net.dto.in.resp.log;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 2/15/2018
 * Time: 11:00 AM
 */
public class Samples {
    private List<VehicleSample> samples;

    public List<VehicleSample> getSamples() {
        return samples;
    }

    public void setSamples(List<VehicleSample> samples) {
        this.samples = samples;
    }
}