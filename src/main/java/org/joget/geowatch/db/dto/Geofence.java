package org.joget.geowatch.db.dto;

import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joget.geowatch.type.ZoneType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 1/20/2018
 * Time: 12:44 PM
 */
@Entity
@Table(name = "app_fd_Geofence")
public class Geofence implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "dateCreated", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "dateModified", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

    @Column(name = "c_name")
    @Type(type = "text")
    private String name;

    @Column(name = "c_lat")
    @Type(type = "text")
    private String lat;

    @Column(name = "c_lng")
    @Type(type = "text")
    private String lng;

    @Column(name = "c_radius")
    @Type(type = "text")
    private String radius;

    @Column(name = "c_polygonHash")
    @Type(type = "text")
    private String polygonHash;

    @Column(name = "c_address")
    @Type(type = "text")
    private String address;

    @SerializedName("type")
    @Column(name = "c_type", columnDefinition="longtext")
    private String zoneType;

    @Column(name = "c_description")
    @Type(type = "text")
    private String description;

    public Geofence() {
    }

    public Geofence(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getPolygonHash() {
        return polygonHash;
    }

    public void setPolygonHash(String polygonHash) {
        this.polygonHash = polygonHash;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZoneType() {
        return zoneType;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Geofence update(Geofence item){
        if(item == null) return null;

        item.dateCreated = item.dateCreated != null ?  item.dateCreated : new Date();
        item.dateModified = new Date();
        return item;
    }
}
