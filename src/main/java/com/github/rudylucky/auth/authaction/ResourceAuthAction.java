package com.github.rudylucky.auth.authaction;

import com.github.rudylucky.auth.dto.DepartmentWithResourceDTO;
import com.github.rudylucky.auth.dto.Resource;
import com.github.rudylucky.auth.dto.ResourceDTO;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;

import java.util.Collection;

public interface ResourceAuthAction {

    ResourceDTO createNonGroupResource(String resourceName, ResourceTypeEnum resourceType, Integer sort);

    void modifyNonGroupResourceName(String resourceName, ResourceTypeEnum resourceType, String newResourceName);

    ResourceDTO createResource(String resourceName, ResourceTypeEnum resourceType, String parentId, Integer sort);

    ResourceDTO modifyResource(String resourceId, String resourceName);

    void revokeResource(String resourceName, ResourceTypeEnum resourceType);

    Resource getRoleResource(String roleId);

    Resource getResource();

    Resource getResourceByRoleId(String roleId);

    Resource getResourceByUserId(String userId);

    Resource deleteResource(String resourceId);

    Resource moveResource(String resourceId, String parentId);

    Resource getUserResource(String userId);

    Collection<String> listResourceNameByResourceType(ResourceTypeEnum resourceType);

    DepartmentWithResourceDTO getDepartmentResourceByCurrentUser();

    Collection<ResourceDTO> getReadableBook();
}
