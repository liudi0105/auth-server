package com.github.rudylucky.auth.dto;

import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class ResourcePermissionDTO {

    private String userId;
    private String resourceId;
    private ResourcePermissionTypeEnum resourcePermission;

    public ResourcePermissionDTO(String userId, String resourceId, ResourcePermissionTypeEnum resourcePermission) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.resourcePermission = resourcePermission;
    }

    @Override
    public boolean equals(Object other) {
        if (Objects.isNull(other) || !(other instanceof ResourcePermissionDTO))
            return false;

        return StringUtils.equals(this.userId, ((ResourcePermissionDTO) other).userId)
                && StringUtils.equals(this.resourceId, ((ResourcePermissionDTO) other).resourceId)
                && Objects.equals(resourcePermission, ((ResourcePermissionDTO) other).resourcePermission);
    }

    @Override
    public int hashCode() {
        int hashCode = Optional.ofNullable(this.userId).map(String::hashCode).orElse(55);
        hashCode += Optional.ofNullable(this.resourceId).map(String::hashCode).orElse(66);
        hashCode += Optional.ofNullable(this.resourcePermission).map(Enum::hashCode).orElse(77);
        return hashCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public ResourcePermissionTypeEnum getResourcePermission() {
        return resourcePermission;
    }

    public void setResourcePermission(ResourcePermissionTypeEnum resourcePermission) {
        this.resourcePermission = resourcePermission;
    }
}
