package org.joget.geowatch.ght.net.dto.in.resp.log.simple;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 5:23 PM
 */
public class Power {
    private Double battery_voltage;
    private String charger_connected;
    private Boolean charging;

    public Double getBattery_voltage() {
        return battery_voltage;
    }

    public void setBattery_voltage(Double battery_voltage) {
        this.battery_voltage = battery_voltage;
    }

    public String getCharger_connected() {
        return charger_connected;
    }

    public void setCharger_connected(String charger_connected) {
        this.charger_connected = charger_connected;
    }

    public Boolean getCharging() {
        return charging;
    }

    public void setCharging(Boolean charging) {
        this.charging = charging;
    }
}
