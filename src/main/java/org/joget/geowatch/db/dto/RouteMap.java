package org.joget.geowatch.db.dto;

import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joget.directory.model.Employment;
import org.joget.directory.model.Group;
import org.joget.directory.model.User;
import org.joget.geowatch.api.dto.in.req.RouteMapInReq;
import org.joget.geowatch.db.dto.converter.WayPointListConverter;
import org.joget.geowatch.db.dto.inner.WayPointInnerEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/5/2018
 * Time: 11:20 AM
 */
@Entity
@Table(name = "app_fd_RouteMap")
public class RouteMap implements Serializable {
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
    @Column(name = "c_name")
    private String name;

    @Type(type = "text")
    @Column(name = "c_polylineHash")
    private String polylineHash;

    @Convert(converter = WayPointListConverter.class)
    @Column(name = "c_wayPoints", columnDefinition="longtext")
    private List<WayPointInnerEntity> wayPointList;

    @Type(type = "text")
    @Column(name = "c_creatorId")
    private String creatorId;

    @Type(type = "text")
    @Column(name = "c_creatorDepartmentId")
    private String creatorDepartmentId;

    @Type(type = "text")
    @Column(name = "c_creatorOrganizationId")
    private String creatorOrganizationId;

    @Type(type = "text")
    @Column(name = "c_creatorGroupsId")
    private String creatorGroupsId;

    public RouteMap() {
    }

    public RouteMap(String id) {
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

    public String getPolylineHash() {
        return polylineHash;
    }

    public void setPolylineHash(String polylineHash) {
        this.polylineHash = polylineHash;
    }

    public List<WayPointInnerEntity> getWayPointList() {
        return wayPointList;
    }

    public void setWayPointList(List<WayPointInnerEntity> wayPointList) {
        this.wayPointList = wayPointList;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorDepartmentId() {
        return creatorDepartmentId;
    }

    public void setCreatorDepartmentId(String creatorDepartmentId) {
        this.creatorDepartmentId = creatorDepartmentId;
    }

    public String getCreatorOrganizationId() {
        return creatorOrganizationId;
    }

    public void setCreatorOrganizationId(String creatorOrganizationId) {
        this.creatorOrganizationId = creatorOrganizationId;
    }

    public String getCreatorGroupsId() {
        return creatorGroupsId;
    }

    public void setCreatorGroupsId(String creatorGroupsId) {
        this.creatorGroupsId = creatorGroupsId;
    }

    public static RouteMap update(RouteMap item, RouteMapInReq routeMapInReq){
        if(item == null) return null;
        if(routeMapInReq == null) return item;

        item.id = routeMapInReq.getId();
        item.name = routeMapInReq.getName();
        item.dateCreated = new Date();
        item.dateModified = new Date();
        item.polylineHash = routeMapInReq.getPolylineHash();
        item.wayPointList = WayPointInnerEntity.update(new ArrayList<WayPointInnerEntity>(), routeMapInReq.getWayPoints());

        return item;
    }

    public static RouteMap update(RouteMap item, User user) {
        if(item == null) return null;
        if(user == null) return item;

        Employment employment = (Employment) user.getEmployments().iterator().next();   //  take only first department
        if(employment !=null) {
            item.setCreatorOrganizationId(employment.getOrganizationId());
            item.setCreatorDepartmentId(employment.getDepartmentId());
            item.setCreatorId(employment.getUserId());
        }

        Group group = null;
        if (user.getGroups() != null && user.getGroups().size() > 0) {
            group = (Group) user.getGroups().iterator().next();     //  have only one group
        }
        item.setCreatorGroupsId(group.getId());
        return item;
    }

}
