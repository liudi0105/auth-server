package com.github.rudylucky.auth.service;

import com.github.rudylucky.auth.dto.Resource;
import com.github.rudylucky.auth.dto.ResourceDTO;

import java.util.Collection;

public interface ResourceService {

    ResourceDTO authResourceCreate(String resourceType, String resourceName, String parentId, Number sort);

    Resource authResourceGet();

    Resource authResourceGetByUserId(String userId);

    Resource authResourceGetByRoleId(String roleId);

    Resource authResourceRevoke(String resourceId);

    Resource authResourceModify(String resourceId, String resourceName, String parentId);

    Collection<String> authResourceList(String resourceType);

    ResourceDTO authNonGroupResourceAdd(String resourceType, String resourceName);

    Boolean authNonGroupResourceRevoke(String resourceType, String resourceName);

    Boolean authNonGroupResourceModify(String resourceName, String resourceType, String newResourceName);

    Collection<ResourceDTO> authBookGetCanRead();
}
