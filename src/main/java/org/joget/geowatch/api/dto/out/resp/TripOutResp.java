package org.joget.geowatch.api.dto.out.resp;

import com.google.gson.annotations.SerializedName;
import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.LogJson;
import org.joget.geowatch.db.dto.Trip;

import java.util.ArrayList;
import java.util.List;

import static org.joget.geowatch.db.dto.type.VehicleType.HAULIER;
import static org.joget.geowatch.db.dto.type.VehicleType.RMO;
import static org.joget.geowatch.db.dto.type.VehicleType.TRAILER;
import static org.joget.geowatch.util.DateUtil.getJogetDate;
import static org.joget.geowatch.util.DateUtil.getUiShortStrDate;

/**
 * Created by k.lebedyantsev
 * Date: 1/17/2018
 * Time: 11:55 AM
 */
public class TripOutResp {
    protected String id;
    protected String startDate;
    protected String endDate;
    protected String liveState;

    protected String name;
    protected TypeOutResp jobType;
    protected RouteMapOutResp route;

    @SerializedName("vehicles")
    protected List<VehicleOutResp> vehicleList;

    @SerializedName("logs")
    protected List<LogJson> logList;

    @SerializedName("notifys")
    protected List<NotifyOutResp> notifyList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeOutResp getJobType() {
        return jobType;
    }

    public void setJobType(TypeOutResp jobType) {
        this.jobType = jobType;
    }

    public RouteMapOutResp getRoute() {
        return route;
    }

    public void setRoute(RouteMapOutResp route) {
        this.route = route;
    }

    public List<VehicleOutResp> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<VehicleOutResp> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public List<LogJson> getLogList() {
        return logList;
    }

    public void setLogList(List<LogJson> logList) {
        this.logList = logList;
    }

    public List<NotifyOutResp> getNotifyList() {
        return notifyList;
    }

    public void setNotifyList(List<NotifyOutResp> notifyList) {
        this.notifyList = notifyList;
    }

    public String getLiveState() {
        return liveState;
    }

    public void setLiveState(String liveState) {
        this.liveState = liveState;
    }

    public static TripOutResp update(TripOutResp item, Trip trip, User user, boolean includeWayPointInfo) throws Exception {
        if (item == null) return null;
        if (trip == null) return item;

        item.id = trip.getId();
        item.name = trip.getName();
        item.jobType = TypeOutResp.getInstance(trip.getJobType());
        item.startDate = getUiShortStrDate(getJogetDate(trip.getStartDateTime()), user);
        item.endDate = getUiShortStrDate(getJogetDate(trip.getFinishDateTime()), user);
        item.liveState = trip.getLiveState().name();

        if (includeWayPointInfo)
            item.setRoute(RouteMapOutResp.update(new RouteMapOutResp(), trip.getImageRouteMap()));

        item.vehicleList = new ArrayList<>();
        if (trip.getHaulierVehicle() != null)
            item.vehicleList.add(VehicleOutResp.update(
                    new VehicleOutResp(), HAULIER, trip.getHaulierVehicle(),
                    trip.getHaulierGhtVehicle(), trip.getHaulierLastPosition(), null));

        if (trip.getHaulierTrailerVehicle() != null)
            item.vehicleList.add(VehicleOutResp.update(
                    new VehicleOutResp(), TRAILER, trip.getHaulierTrailerVehicle(),
                    trip.getHaulierTrailerGhtVehicle(), trip.getHaulierTrailerLastPosition(), null));

        if (trip.getRmoVehicle() != null)
            item.vehicleList.add(VehicleOutResp.update(
                    new VehicleOutResp(), RMO, trip.getRmoVehicle(),
                    trip.getRmoGhtVehicle(), trip.getRmoLastPosition(), null));

        return item;
    }

    @Override
    public String toString() {
        return "TripOutResp{" +
                "id='" + id + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
