package org.joget.geowatch.db.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by k.lebedyantsev
 * Date: 1/16/2018
 * Time: 5:17 PM
 */
@Entity
@Table(name = "dir_department")
public class Department {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "organizationId")
    private String organizationId;

    @Column(name = "hod")
    private String hod;

    @Column(name = "parentId")
    private String parentId;

    public Department(String id, String name, String description, String organizationId, String hod, String parentId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
        this.hod = hod;
        this.parentId = parentId;
    }

    public Department() {
    }

    public Department(String id) {
        this.id = id;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getHod() {
        return hod;
    }

    public void setHod(String hod) {
        this.hod = hod;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", organizationId='" + organizationId + '\'' +
                ", hod='" + hod + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
