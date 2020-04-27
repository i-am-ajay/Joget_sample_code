package org.joget.geowatch.db.dto.inner;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteMapInnerEntity {

    private String id;
    private String name;
    private String polylineHash;

    @SerializedName("wayPoints")
    private List<WayPointInnerEntity> wayPointList;

    private String creatorId;
    private String creatorDepartmentId;
    private String creatorOrganizationId;
    private String creatorGroupsId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
