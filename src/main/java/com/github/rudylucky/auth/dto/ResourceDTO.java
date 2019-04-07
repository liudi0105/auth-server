package com.github.rudylucky.auth.dto;

import com.github.rudylucky.auth.common.util.tree.PlainTreeRecord;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;

public class ResourceDTO implements PlainTreeRecord {

    @Override
    public Integer getSort() {
        return 0;
    }

    private String id;
    private String resourceName;
    private ResourceTypeEnum resourceType;
    private String parentId;
    private String departmentId;
    private String createTime;

    public ResourceDTO(String id, String resourceName, ResourceTypeEnum resourceType, String parentId, String departmentId, String createTime) {
        this.id = id;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.parentId = parentId;
        this.departmentId = departmentId;
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
}
