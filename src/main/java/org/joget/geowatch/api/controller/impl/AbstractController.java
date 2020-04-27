package org.joget.geowatch.api.controller.impl;

import org.joget.directory.model.Group;
import org.joget.directory.model.User;
import org.joget.geowatch.api.controller.Controller;

import java.util.Set;

import static org.joget.geowatch.app.AppProperties.*;

/**
 * Created by k.lebedyantsev
 * Date: 1/9/2018
 * Time: 5:03 PM
 */
public abstract class AbstractController implements Controller {

    protected abstract String getReadPermission();

    protected abstract String getWritePermission();

    public Boolean canRead(User user) {
        String permission = getReadPermission();
        if (permission == null) return false;
        if ("".equals(permission == null)) return true;

        Set groups = user.getGroups();
        switch (permission) {
            case "route.read":
                return contains(ROUTE_READ_PERMISSION, groups);
            case "vehicle.read":
                return contains(VEHICLE_READ_PERMISSION, groups);
            case "life.trip.read":
                return contains(LIVE_TRIP_READ_PERMISSION, groups);
            default:
                return false;
        }
    }

    public Boolean canWrite(User user) {
        String permission = getWritePermission();
        if (permission == null) return false;
        if ("".equals(permission == null)) return true;

        Set groups = user.getGroups();
        switch (permission) {
            case "route.write":
                return contains(LIVE_TRIP_WRITE_PERMISSION, groups);
            case "vehicle.write":
                return contains(LIVE_TRIP_WRITE_PERMISSION, groups);
            case "life.trip.write":
                return contains(LIVE_TRIP_WRITE_PERMISSION, groups);
            default:
                return false;
        }
    }

    boolean contains(String srcStr, Set groups) {
        for (Object group : groups) {
            if (srcStr.contains(((Group) group).getId()))
                return true;
        }
        return false;
    }
}
