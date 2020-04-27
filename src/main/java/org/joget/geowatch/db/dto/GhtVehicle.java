package org.joget.geowatch.db.dto;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 1:30 PM
 */
@Entity
@Table(name = "app_fd_GhtVehicle")
public class GhtVehicle implements Serializable {
    @Expose
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "dateCreated", columnDefinition = "DATETIME", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "dateModified", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

    @Type(type = "text")
    @Column(name = "c_vehicleId")
    private String vehicleId;

    @Type(type = "text")
    @Column(name = "c_licensePlate")
    private String licensePlate;

    @Type(type = "text")
    @Column(name = "c_name")
    private String name;

    @Type(type = "text")
    @Column(name = "c_chassisNumber")
    private String chassisNumber;

    @Column(nullable = false, name = "c_operational")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean operational;

    public GhtVehicle() {
    }

    public GhtVehicle(String id) {
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public Boolean getOperational() {
        return operational;
    }

    public void setOperational(Boolean operational) {
        this.operational = operational;
    }

    @Override
    public String toString() {
        return "VehicleSampleGroup{" +
                "id='" + id + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", vehicleId='" + vehicleId + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", name='" + name + '\'' +
                ", chassisNumber='" + chassisNumber + '\'' +
                '}';
    }
}
