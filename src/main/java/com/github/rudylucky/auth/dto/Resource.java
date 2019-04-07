package com.github.rudylucky.auth.dto;

import com.github.rudylucky.auth.common.util.tree.TreeEntity;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;

import java.util.Collection;

public class Resource extends TreeEntity<Resource> {

    private String resourceName;
    private ResourceTypeEnum resourceType;
    private String departmentId;
    private Collection<ResourcePermissionTypeEnum> resourcePermissions;

    public Resource(String id, Integer sort, Resource parent
            , String resourceName, ResourceTypeEnum resourceType, String departmentId, Collection<ResourcePermissionTypeEnum> resourcePermissions) {
        super(id, sort, parent);
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.departmentId = departmentId;
        this.resourcePermissions = resourcePermissions;
    }

    public Resource(String id, Integer sort, String resourceName
            , ResourceTypeEnum resourceType, String departmentId, Collection<ResourcePermissionTypeEnum> resourcePermissions) {
        super(id, sort);
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.departmentId = departmentId;
        this.resourcePermissions = resourcePermissions;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public ResourceTypeEnum getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public Collection<ResourcePermissionTypeEnum> getResourcePermissions() {
        return resourcePermissions;
    }

    public void setResourcePermissions(Collection<ResourcePermissionTypeEnum> resourcePermissions) {
        this.resourcePermissions = resourcePermissions;
    }
}
