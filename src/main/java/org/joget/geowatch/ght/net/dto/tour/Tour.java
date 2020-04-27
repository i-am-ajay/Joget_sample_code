package org.joget.geowatch.ght.net.dto.tour;

import com.google.gson.annotations.SerializedName;
import org.joget.geowatch.db.dto.Trip;
import org.joget.geowatch.db.dto.inner.GhtVehicleInnerEntity;

import java.util.ArrayList;
import java.util.List;

import static org.joget.geowatch.util.DateUtil.getGhtStrDate;
import static org.joget.geowatch.util.DateUtil.getJogetDate;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 11:33 AM
 */
public class Tour {
    private String id;//Unique id for the tour
    private String route_name;//Used to identify the route, e.g. a vehicle may drive the same route every day, these tours can be idetified by assigning them the same route_name
    private String user;
    private String license_plate;//Vehicle license plate
    private String license_plate_alternative_1;//Vehicle license plate, the system will search for this if license_plate does not exists
    private String license_plate_alternative_2;//Vehicle license plate, the system will search for this if license_plate and license_plate_alternative_1 does not exists
    private String vehicle_id;//Vehicle id, e.g. a fleet identifier
    private String vehicle_id_alternative_1;//Vehicle id, the system will search for this if vehicle_id does not exists
    private String vehicle_id_alternative_2;//Vehicle id, the system will search for this if vehicle_id and vehicle_id_alternative_1 does not exists
    private String hauler;
    private String customer;//see mail. it set from api_config.properties

    @SerializedName("transport_company")
    private String transportCompany;
    private String planned_begin;//UTC, format ISO-8601
    private String planned_end;//UTC, format ISO-8601
    private List<Location> stops = new ArrayList<>();
//    private List<VehicleChange> vehicle_change = new ArrayList<>();

    // if this parameters need then ask ghTrack about this field
    //    private String type;
//    private List<Notify> notifys;
    //tags object ignore.if it need see ghtrack_v2_api.pdf
//    private String vehicle_owner_code;
//    private String base_id;

//    private String activation_mode = "normal";//by business logic user at joget approve life trip and then tour created with active status

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public String getLicense_plate_alternative_1() {
        return license_plate_alternative_1;
    }

    public void setLicense_plate_alternative_1(String license_plate_alternative_1) {
        this.license_plate_alternative_1 = license_plate_alternative_1;
    }

    public String getLicense_plate_alternative_2() {
        return license_plate_alternative_2;
    }

    public void setLicense_plate_alternative_2(String license_plate_alternative_2) {
        this.license_plate_alternative_2 = license_plate_alternative_2;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle_id_alternative_1() {
        return vehicle_id_alternative_1;
    }

    public void setVehicle_id_alternative_1(String vehicle_id_alternative_1) {
        this.vehicle_id_alternative_1 = vehicle_id_alternative_1;
    }

    public String getVehicle_id_alternative_2() {
        return vehicle_id_alternative_2;
    }

    public void setVehicle_id_alternative_2(String vehicle_id_alternative_2) {
        this.vehicle_id_alternative_2 = vehicle_id_alternative_2;
    }

    public String getHauler() {
        return hauler;
    }

    public void setHauler(String hauler) {
        this.hauler = hauler;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(String transportCompany) {
        this.transportCompany = transportCompany;
    }

    public String getPlanned_begin() {
        return planned_begin;
    }

    public void setPlanned_begin(String planned_begin) {
        this.planned_begin = planned_begin;
    }

    public String getPlanned_end() {
        return planned_end;
    }

    public void setPlanned_end(String planned_end) {
        this.planned_end = planned_end;
    }

    public List<Location> getStops() {
        return stops;
    }

    public void setStops(List<Location> stops) {
        this.stops = stops;
    }

//    public String getActivation_mode() {
//        return activation_mode;
//    }
//
//    public void setActivation_mode(String activation_mode) {
//        this.activation_mode = activation_mode;
//    }

    public static Tour update(Tour tour, Trip trip, GhtVehicleInnerEntity ghtVehicle, String customer, String transportCompany) throws Exception {
        tour.id = trip.getId() + "_" + ghtVehicle.getId();
        tour.customer = customer;
        tour.transportCompany = transportCompany;
        tour.license_plate = ghtVehicle.getId();
        tour.planned_begin = getGhtStrDate(getJogetDate(trip.getStartDateTime()));
        tour.planned_end = getGhtStrDate(getJogetDate(trip.getFinishDateTime()));
        tour.stops = Location.update(new ArrayList<Location>(), trip.getImageRouteMap().getWayPointList());

        return tour;
    }
}
