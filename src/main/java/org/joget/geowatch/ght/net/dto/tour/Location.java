package org.joget.geowatch.ght.net.dto.tour;

import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;
import org.joget.geowatch.ght.net.dto.in.resp.log.Address;

import java.util.List;

import static org.joget.geowatch.ght.net.dto.tour.LocationType.pickup_and_delivery;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 11:59 AM
 */
public class Location {
    private String id;//Unique ID for the location, must be unique within the tour
    private LocationType type;
    private String time_window_begin;//UTC, format ISO-8601
    private String time_window_end;//UTC, format ISO-8601
    private String rta;//Required time of arrival, UTC, format ISO-8601
    private String rta_max;//Required time of arrival maximum, UTC, format ISO-8601
    private Integer estimated_work_duration;//Estimated work duration in seconds at location
    private Integer estimated_pause_duration;//Estimated pause duration in seconds at location
    private Address address = new Address();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    public String getTime_window_begin() {
        return time_window_begin;
    }

    public void setTime_window_begin(String time_window_begin) {
        this.time_window_begin = time_window_begin;
    }

    public String getTime_window_end() {
        return time_window_end;
    }

    public void setTime_window_end(String time_window_end) {
        this.time_window_end = time_window_end;
    }

    public String getRta() {
        return rta;
    }

    public void setRta(String rta) {
        this.rta = rta;
    }

    public String getRta_max() {
        return rta_max;
    }

    public void setRta_max(String rta_max) {
        this.rta_max = rta_max;
    }

    public Integer getEstimated_work_duration() {
        return estimated_work_duration;
    }

    public void setEstimated_work_duration(Integer estimated_work_duration) {
        this.estimated_work_duration = estimated_work_duration;
    }

    public Integer getEstimated_pause_duration() {
        return estimated_pause_duration;
    }

    public void setEstimated_pause_duration(Integer estimated_pause_duration) {
        this.estimated_pause_duration = estimated_pause_duration;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public static List<Location> update(List<Location> itemList, List<WayPointInnerEntity> wayPointList) {
        if (itemList == null) return itemList;
        if (wayPointList == null) return itemList;

        for(int i = 0; i < wayPointList.size(); i++)
            itemList.add(update(new Location(), wayPointList.get(i), i));

        return itemList;
    }

    public static Location update(Location item, WayPointInnerEntity wayPoint, int number) {
        if (item == null) return null;
        item.id = number+"";
        item.type = pickup_and_delivery;
        item.address = Address.update(new Address(), wayPoint);
        return item;
    }
}
