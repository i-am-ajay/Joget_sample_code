package org.joget.geowatch.ght.net.dto.in.resp.log;

import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/3/2018
 * Time: 5:06 PM
 */
public class Address {
    private String location_id;
    private String customer_name;
    private String zipcode;
    private String city;
    private String country;
    private String street;
    private Double latitude;
    private Double longitude;
    private Integer score;
    private Long geofence_radius;

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getGeofence_radius() {
        return geofence_radius;
    }

    public void setGeofence_radius(Long geofence_radius) {
        this.geofence_radius = geofence_radius;
    }

    public static List<Address> update(List<Address> list, List<WayPointInnerEntity> wayPointList) {
        if (list == null) return list;
        if (wayPointList == null) return list;

        for (WayPointInnerEntity wp : wayPointList)
            list.add(Address.update(new Address(), wp));

        return list;
    }

    public static Address update(Address item, WayPointInnerEntity wayPoint) {
        if (item == null) return item;
        item.latitude = wayPoint.getLat();
        item.longitude = wayPoint.getLng();
        return item;
    }
}
