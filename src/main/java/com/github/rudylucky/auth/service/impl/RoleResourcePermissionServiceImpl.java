package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.authaction.ResourceAuthAction;
import com.github.rudylucky.auth.authaction.RoleResourcePermissionAuthAction;
import com.github.rudylucky.auth.common.api.annotation.BctMethodArg;
import com.github.rudylucky.auth.common.api.annotation.BctMethodInfo;
import com.github.rudylucky.auth.common.exception.AuthServiceException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.dto.Resource;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.service.ApiParamConstants;
import com.github.rudylucky.auth.service.RoleResourcePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
public class RoleResourcePermissionServiceImpl implements RoleResourcePermissionService {

    private ResourceAuthAction resourceAuthAction;
    private RoleResourcePermissionAuthAction roleResourcePermissionAuthAction;

    @Autowired
    public RoleResourcePermissionServiceImpl(
            ResourceAuthAction resourceAuthAction
            , RoleResourcePermissionAuthAction roleResourcePermissionAuthAction){
        this.resourceAuthAction = resourceAuthAction;
        this.roleResourcePermissionAuthAction = roleResourcePermissionAuthAction;
    }

    @BctMethodInfo(
            description = "modify permissions for a role & a resource",
            retName = "resource tree of this role",
            retDescription = "resource")
    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Resource authRolePermissionsModify(
            @BctMethodArg(name = ApiParamConstants.ROLE_ID, description = "role id") String roleId,
            @BctMethodArg(name = ApiParamConstants.PERMISSIONS, description = "permissions") List<Map<String, Object>> permissions){
        if(CollectionUtils.isEmpty(permissions))
            throw new AuthServiceException( ReturnMessageAndTemplateDef.Errors.MISSING_RESOURCE_PERMISSIONS);

        permissions.forEach(map -> {
            String resourceId = (String) map.get(ApiParamConstants.RESOURCE_ID);
            List<ResourcePermissionTypeEnum> resourcePermissionTypes =
                    ResourcePermissionTypeEnum.ofList((List<String>) map.get(ApiParamConstants.RESOURCE_PERMISSION));
            roleResourcePermissionAuthAction.modifyRoleResourcePermissions(roleId, resourceId, resourcePermissionTypes);
        });

        return resourceAuthAction.getResourceByRoleId(roleId);
    }

}
