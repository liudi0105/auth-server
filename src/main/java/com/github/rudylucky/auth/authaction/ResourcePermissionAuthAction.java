package com.github.rudylucky.auth.authaction;

import com.github.rudylucky.auth.dto.ResourcePermissionDTO;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;

import java.util.Collection;
import java.util.List;

public interface ResourcePermissionAuthAction {

    Boolean hasDepartmentResourcePermissionForCurrentUser(ResourcePermissionTypeEnum resourcePermissionType);

    Boolean hasDepartmentResourcePermissionForCurrentUser(String departmentId, ResourcePermissionTypeEnum resourcePermissionType);

    Boolean hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum resourcePermissionType);

    Collection<ResourcePermissionDTO> createResourcePermissions(String userId, String resourceId, Collection<ResourcePermissionTypeEnum> resourcePermissionTypeSet);

    Boolean hasResourcePermissionForCurrentUser(String resourceId, ResourcePermissionTypeEnum resourcePermissionType);

    List<Boolean> hasResourcePermissionForCurrentUser(String resourceName, ResourceTypeEnum resourceType, List<ResourcePermissionTypeEnum> resourcePermissionTypeList);

    List<Boolean> hasResourcePermissionForCurrentUser(List<String> resourceName, ResourceTypeEnum resourceTypeEnum, ResourcePermissionTypeEnum resourcePermissionType);

    Collection<ResourcePermissionDTO> modifyResourcePermissions(String userId, String resourceId, Collection<ResourcePermissionTypeEnum> resourcePermissionTypes);

    UserDTO modifyUserRole(String userId, List<String> roleIds);
}
