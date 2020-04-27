package org.joget.geowatch.db.dto;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joget.geowatch.db.dto.converter.RouteMapInnerEntityConverter;
import org.joget.geowatch.db.dto.inner.RouteMapInnerEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by k.lebedyantsev
 * Date: 1/22/2018
 * Time: 4:25 PM
 */
@Entity
@Table(name = "app_fd_Route")
public class Route implements Serializable {
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

    @Type(type = "text")
    @Column(name = "c_status")
    private String status;

    @Type(type = "text")
    @Column(name = "c_name")
    private String name;

    @Type(type = "text")
    @Column(name = "c_requesterId")
    private String requesterId;

//    @Type(type = "text")
//    @Column(name = "c_requesterOrganizationName")
//    private String requesterOrganizationName;

    @Type(type = "text")
    @Column(name = "c_requesterGroupsId")
    private String requesterGroupsId;

//    @Type(type = "text")
//    @Column(name = "c_requesterDepartmentName")
//    private String requesterDepartmentName;

    @Type(type = "text")
    @Column(name = "c_requesterDepartmentId")
    private String requesterDepartmentId;

//    @Type(type = "text")
//    @Column(name = "c_requesterGroupsName")
//    private String requesterGroupsName;

    @Type(type = "text")
    @Column(name = "c_loadingAddress")
    private String loadingAddress;

//    @Type(type = "text")
//    @Column(name = "c_comments")
//    private String comments;

    @Type(type = "text")
    @Column(name = "c_deliveryAddress")
    private String deliveryAddress;

//    @Type(type = "text")
//    @Column(name = "c_deliveryDocument")
//    private String deliveryDocument;

//    @Type(type = "text")
//    @Column(name = "c_wayPoints")
//    private String wayPoints;

//    @Type(type = "text")
//    @Column(name = "c_polylineHash")
//    private String polylineHash;

//    @Type(type = "text")
//    @Column(name = "c_polygonHash")
//    private String polygonHash;

    @Type(type = "text")
    @Column(name = "c_routeMapId")
    private String routeMapId;

//    @Type(type = "text")
//    @Column(name = "c_nameRouteMap")
//    private String nameRouteMap;

    @Convert(converter = RouteMapInnerEntityConverter.class)
    @Column(name = "c_imageRouteMap", columnDefinition="longtext", updatable = false)
    private RouteMapInnerEntity imageRouteMap;

//    @Type(type = "text")
//    @Column(name = "c_routeSnapshot")
//    private String routeSnapshot;

    public Route() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterGroupsId() {
        return requesterGroupsId;
    }

    public void setRequesterGroupsId(String requesterGroupsId) {
        this.requesterGroupsId = requesterGroupsId;
    }

    public String getRequesterDepartmentId() {
        return requesterDepartmentId;
    }

    public void setRequesterDepartmentId(String requesterDepartmentId) {
        this.requesterDepartmentId = requesterDepartmentId;
    }

    public String getLoadingAddress() {
        return loadingAddress;
    }

    public void setLoadingAddress(String loadingAddress) {
        this.loadingAddress = loadingAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getRouteMapId() {
        return routeMapId;
    }

    public void setRouteMapId(String routeMapId) {
        this.routeMapId = routeMapId;
    }

    public RouteMapInnerEntity getImageRouteMap() {
        return imageRouteMap;
    }

    public void setImageRouteMap(RouteMapInnerEntity imageRouteMap) {
        this.imageRouteMap = imageRouteMap;
    }
}
