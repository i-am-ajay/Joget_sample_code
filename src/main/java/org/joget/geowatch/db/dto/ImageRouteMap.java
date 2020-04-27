package org.joget.geowatch.db.dto;

import org.joget.geowatch.api.dto.WayPoint;

import java.util.Date;
import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/5/2018
 * Time: 11:20 AM
 */

public class ImageRouteMap {

    private String id;
    private Date dateCreated;
    private Date dateModified;
    private List<WayPoint> wayPointList;
    private String creatorOrganizationId;
    private String creatorDepartmentName;
    private String creatorId;
    private String creatorOrganizationName;
    private String creatorName;
    private String creatorDepartmentId;
    private String name;
    private String creatorGroupsId;
    private String creatorGroupsName;
    private String wayPointsQuantity;
    private String polylineHash;
    private String routeSnapshot;

    public ImageRouteMap() {
    }

    public ImageRouteMap(String id) {
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

    public List<WayPoint> getWayPointList() {
        return wayPointList;
    }

    public void setWayPointList(List<WayPoint> wayPointList) {
        this.wayPointList = wayPointList;
    }

    public String getCreatorOrganizationId() {
        return creatorOrganizationId;
    }

    public void setCreatorOrganizationId(String creatorOrganizationId) {
        this.creatorOrganizationId = creatorOrganizationId;
    }

    public String getCreatorDepartmentName() {
        return creatorDepartmentName;
    }

    public void setCreatorDepartmentName(String creatorDepartmentName) {
        this.creatorDepartmentName = creatorDepartmentName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorOrganizationName() {
        return creatorOrganizationName;
    }

    public void setCreatorOrganizationName(String creatorOrganizationName) {
        this.creatorOrganizationName = creatorOrganizationName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorDepartmentId() {
        return creatorDepartmentId;
    }

    public void setCreatorDepartmentId(String creatorDepartmentId) {
        this.creatorDepartmentId = creatorDepartmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorGroupsId() {
        return creatorGroupsId;
    }

    public void setCreatorGroupsId(String creatorGroupsId) {
        this.creatorGroupsId = creatorGroupsId;
    }

    public String getCreatorGroupsName() {
        return creatorGroupsName;
    }

    public void setCreatorGroupsName(String creatorGroupsName) {
        this.creatorGroupsName = creatorGroupsName;
    }

    public String getWayPointsQuantity() {
        return wayPointsQuantity;
    }

    public void setWayPointsQuantity(String wayPointsQuantity) {
        this.wayPointsQuantity = wayPointsQuantity;
    }

    public String getPolylineHash() {
        return polylineHash;
    }

    public void setPolylineHash(String polylineHash) {
        this.polylineHash = polylineHash;
    }

    public String getRouteSnapshot() {
        return routeSnapshot;
    }

    public void setRouteSnapshot(String routeSnapshot) {
        this.routeSnapshot = routeSnapshot;
    }
}
