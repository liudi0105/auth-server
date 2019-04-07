package com.github.rudylucky.auth.authaction;

import com.github.rudylucky.auth.dto.RoleResourcePermissionDTO;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;

import java.util.Collection;

public interface RoleResourcePermissionAuthAction {
    Collection<RoleResourcePermissionDTO> modifyRoleResourcePermissions(String roleId, String resourceId, Collection<ResourcePermissionTypeEnum> resourcePermissionTypes);
}
