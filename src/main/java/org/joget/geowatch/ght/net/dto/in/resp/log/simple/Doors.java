package org.joget.geowatch.ght.net.dto.in.resp.log.simple;

/**
 * Created by k.lebedyantsev
 * Date: 2/15/2018
 * Time: 11:16 AM
 */
public class Doors {
    private String timestamp;
    private Boolean door1_open;
    private Boolean door2_open;
    private Boolean door3_open;
    private Boolean door4_open;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getDoor1_open() {
        return door1_open;
    }

    public void setDoor1_open(Boolean door1_open) {
        this.door1_open = door1_open;
    }

    public Boolean getDoor2_open() {
        return door2_open;
    }

    public void setDoor2_open(Boolean door2_open) {
        this.door2_open = door2_open;
    }

    public Boolean getDoor3_open() {
        return door3_open;
    }

    public void setDoor3_open(Boolean door3_open) {
        this.door3_open = door3_open;
    }

    public Boolean getDoor4_open() {
        return door4_open;
    }

    public void setDoor4_open(Boolean door4_open) {
        this.door4_open = door4_open;
    }
}
