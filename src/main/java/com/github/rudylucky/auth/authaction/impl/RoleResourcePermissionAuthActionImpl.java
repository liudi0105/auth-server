package com.github.rudylucky.auth.authaction.impl;

import com.github.rudylucky.auth.authaction.ResourcePermissionAuthAction;
import com.github.rudylucky.auth.authaction.RoleResourcePermissionAuthAction;
import com.github.rudylucky.auth.dto.ResourceDTO;
import com.github.rudylucky.auth.dto.RoleResourcePermissionDTO;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.common.exception.AuthorizationException;
import com.github.rudylucky.auth.manager.ResourceManager;
import com.github.rudylucky.auth.manager.RoleResourcePermissionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Component
public class RoleResourcePermissionAuthActionImpl implements RoleResourcePermissionAuthAction {

    private ResourcePermissionAuthAction resourcePermissionAuthAction;
    private RoleResourcePermissionManager roleResourcePermissionManager;
    private ResourceManager resourceManager;

    public RoleResourcePermissionAuthActionImpl(
            ResourcePermissionAuthAction resourcePermissionAuthAction
            , RoleResourcePermissionManager roleResourcePermissionManager
            , ResourceManager resourceManager){
        this.resourcePermissionAuthAction = resourcePermissionAuthAction;
        this.roleResourcePermissionManager = roleResourcePermissionManager;
        this.resourceManager = resourceManager;
    }

    @Override
    @Transactional
    public Collection<RoleResourcePermissionDTO> modifyRoleResourcePermissions(String roleId, String resourceId, Collection<ResourcePermissionTypeEnum> resourcePermissionTypes) {
        if(!resourcePermissionAuthAction.hasResourcePermissionForCurrentUser(resourceId, ResourcePermissionTypeEnum.GRANT_ACTION)
                && !resourcePermissionAuthAction.hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum.GRANT_ACTION)) {

            ResourceDTO resource = resourceManager.getResource(resourceId);
            throw new AuthorizationException(resource.getResourceType(), resource.getResourceName(), ResourcePermissionTypeEnum.GRANT_ACTION);
        }

        return roleResourcePermissionManager.modifyRoleResourcePermissions(roleId, resourceId, resourcePermissionTypes);
    }
}
