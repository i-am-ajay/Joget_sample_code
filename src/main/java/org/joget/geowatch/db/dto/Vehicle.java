package org.joget.geowatch.db.dto;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 1/17/2018
 * Time: 3:26 PM
 */
@Entity
@Table(name = "app_fd_Vehicle")
public class Vehicle implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "dateCreated", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "dateModified", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

    @Type(type = "text")
    @Column(name = "c_requesterOrganizationId")
    private String requesterOrganizationId;

    @Type(type = "text")
    @Column(name = "c_tapa_tsr")
    private String tapaTsr;

    @Type(type = "text")
    @Column(name = "c_phone")
    private String phone;

    @Type(type = "text")
    @Column(name = "c_status")
    private String status;

    @Type(type = "text")
    @Column(name = "c_ghtVehicleId")
    private String ghtVehicleId;

    @Type(type = "text")
    @Column(name = "c_ghtTourId")
    private String ghtTourId;

    @Type(type = "text")
    @Column(name = "c_requesterId")
    private String requesterId;

    @Type(type = "text")
    @Column(name = "c_panicButton")
    private String panicButton;

    @Type(type = "text")
    @Column(name = "c_type")
    private String type;

    @Type(type = "text")
    @Column(name = "c_doorAlarm")
    private String doorAlarm;

    @Type(type = "text")
    @Column(name = "c_description")
    private String description;

    @Type(type = "text")
    @Column(name = "c_name")
    private String name;

    @Type(type = "text")
    @Column(name = "c_requesterDepartmentId")
    private String requesterDepartmentId;

    @Type(type = "text")
    @Column(name = "c_license")
    private String license;

    @Type(type = "text")
    @Column(name = "c_requesterGroupsId")
    private String requesterGroupsId;

    @Type(type = "text")
    @Column(name = "c_comments")
    private String comments;

    @Type(type = "text")
    @Column(name = "lastComment")
    private String lastComment;

    @Type(type = "text")
    @Column(name = "c__id")
    private String c__id;

    public Vehicle(String id, Date dateCreated, Date dateModified, String requesterOrganizationId, String tapaTsr, String phone, String status, String ghtVehicleId, String requesterId, String panicButton, String type, String doorAlarm, String description, String name, String requesterDepartmentId, String license, String requesterGroupsId, String comments, String lastComment) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.requesterOrganizationId = requesterOrganizationId;
        this.tapaTsr = tapaTsr;
        this.phone = phone;
        this.status = status;
        this.ghtVehicleId = ghtVehicleId;
        this.requesterId = requesterId;
        this.panicButton = panicButton;
        this.type = type;
        this.doorAlarm = doorAlarm;
        this.description = description;
        this.name = name;
        this.requesterDepartmentId = requesterDepartmentId;
        this.license = license;
        this.requesterGroupsId = requesterGroupsId;
        this.comments = comments;
        this.lastComment = lastComment;
    }

    public Vehicle() {
    }

    public Vehicle(String id) {
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

    public String getRequesterOrganizationId() {
        return requesterOrganizationId;
    }

    public void setRequesterOrganizationId(String requesterOrganizationId) {
        this.requesterOrganizationId = requesterOrganizationId;
    }

    public String getTapaTsr() {
        return tapaTsr;
    }

    public void setTapaTsr(String tapaTsr) {
        this.tapaTsr = tapaTsr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGhtVehicleId() {
        return ghtVehicleId;
    }

    public void setGhtVehicleId(String ghtVehicleId) {
        this.ghtVehicleId = ghtVehicleId;
    }

    public String getGhtTourId() {
        return ghtTourId;
    }

    public void setGhtTourId(String ghtTourId) {
        this.ghtTourId = ghtTourId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getPanicButton() {
        return panicButton;
    }

    public void setPanicButton(String panicButton) {
        this.panicButton = panicButton;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDoorAlarm() {
        return doorAlarm;
    }

    public void setDoorAlarm(String doorAlarm) {
        this.doorAlarm = doorAlarm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequesterDepartmentId() {
        return requesterDepartmentId;
    }

    public void setRequesterDepartmentId(String requesterDepartmentId) {
        this.requesterDepartmentId = requesterDepartmentId;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getRequesterGroupsId() {
        return requesterGroupsId;
    }

    public void setRequesterGroupsId(String requesterGroupsId) {
        this.requesterGroupsId = requesterGroupsId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLastComment() {
        return lastComment;
    }

    public void setLastComment(String lastComment) {
        this.lastComment = lastComment;
    }

    public String getC__id() {
        return c__id;
    }

    public void setC__id(String c__id) {
        this.c__id = c__id;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                '}';
    }
}
