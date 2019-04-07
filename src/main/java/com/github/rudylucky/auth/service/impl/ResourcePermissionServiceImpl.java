package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.authaction.ResourceAuthAction;
import com.github.rudylucky.auth.authaction.ResourcePermissionAuthAction;
import com.github.rudylucky.auth.authaction.RoleResourcePermissionAuthAction;
import com.github.rudylucky.auth.common.api.annotation.BctMethodArg;
import com.github.rudylucky.auth.common.api.annotation.BctMethodInfo;
import com.github.rudylucky.auth.dto.Resource;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;
import com.github.rudylucky.auth.common.exception.AuthBlankParamException;
import com.github.rudylucky.auth.common.exception.AuthServiceException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.service.ApiParamConstants;
import com.github.rudylucky.auth.service.ResourcePermissionService;
import com.github.rudylucky.auth.common.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourcePermissionServiceImpl implements ResourcePermissionService {

    private ResourcePermissionAuthAction resourcePermissionAuthAction;
    private ResourceAuthAction resourceAuthAction;
    private RoleResourcePermissionAuthAction roleResourcePermissionAuthAction;

    @Autowired
    public ResourcePermissionServiceImpl(
            ResourceAuthAction resourceAuthAction
            , ResourcePermissionAuthAction resourcePermissionAuthAction
            , RoleResourcePermissionAuthAction roleResourcePermissionAuthAction){
        this.resourcePermissionAuthAction = resourcePermissionAuthAction;
        this.resourceAuthAction = resourceAuthAction;
        this.roleResourcePermissionAuthAction = roleResourcePermissionAuthAction;
    }

    @BctMethodInfo(
            description = "add permission(s) for a user to a resource",
            retName = "result",
            retDescription = "resource for given user"
    )
    @Transactional
    public Resource authPermissionsAdd(
            @BctMethodArg(name = ApiParamConstants.USER_ID, description = "The id of a user") String userId,
            @BctMethodArg(name = ApiParamConstants.RESOURCE_ID, description = "resource id") String resourceId,
            @BctMethodArg(name = ApiParamConstants.PERMISSIONS, description = "A list of permitted actions") List<String> permissions) {

        CommonUtils.checkBlankParam(new HashMap<String,String>() {{
            put(ApiParamConstants.USER_ID, userId);
            put(ApiParamConstants.RESOURCE_ID, resourceId);
        }});

        if(CollectionUtils.isEmpty(permissions))
            throw new AuthServiceException( ReturnMessageAndTemplateDef.Errors.MISSING_RESOURCE_PERMISSIONS);

        resourcePermissionAuthAction.createResourcePermissions(userId, resourceId, ResourcePermissionTypeEnum.ofList(permissions));

        return resourceAuthAction.getUserResource(userId);
    }

    @BctMethodInfo(
            description = "modify permissions for a user & a resource",
            retName = "Resource",
            retDescription = "resource")
    @SuppressWarnings("unchecked")
    @Transactional
    public Resource authPermissionsModify(
            @BctMethodArg(name = ApiParamConstants.USER_ID, description = "user id") String userId,
            @BctMethodArg(name = ApiParamConstants.PERMISSIONS, description = "permissions") List<Map<String, Object>> permissions){

        CommonUtils.checkBlankParam(new HashMap<String,String>() {{
            put(ApiParamConstants.USER_ID, userId);
        }});

        if(CollectionUtils.isEmpty(permissions))
            throw new AuthServiceException( ReturnMessageAndTemplateDef.Errors.MISSING_RESOURCE_PERMISSIONS);

        permissions.forEach(map -> {
            String resourceId = (String) map.get(ApiParamConstants.RESOURCE_ID);
            List<ResourcePermissionTypeEnum> resourcePermissionTypes = ResourcePermissionTypeEnum.ofList((List<String>) map.get(ApiParamConstants.RESOURCE_PERMISSION));
            resourcePermissionAuthAction.modifyResourcePermissions(userId, resourceId, resourcePermissionTypes);
        });

        return resourceAuthAction.getUserResource(userId);
    }


    @BctMethodInfo(
            description = "test if an action can do or not",
            retName = "List of Boolean",
            retDescription = "list of boolean"
    )
    @Transactional
    public List<Boolean> authCan(
            @BctMethodArg(name = ApiParamConstants.RESOURCE_TYPE) String resourceType,
            @BctMethodArg(name = ApiParamConstants.RESOURCE_NAME) List<String> resourceName,
            @BctMethodArg(name = ApiParamConstants.RESOURCE_PERMISSION) String resourcePermissionType
    ){

        CommonUtils.checkBlankParam(new HashMap<String,String>() {{
            put(ApiParamConstants.RESOURCE_TYPE, resourceType);
            put(ApiParamConstants.RESOURCE_PERMISSION_TYPE, resourcePermissionType);
        }});

        if(CollectionUtils.isEmpty(resourceName))
            throw new AuthServiceException( ReturnMessageAndTemplateDef.Errors.MISSING_PARAM_RESOURCE_NAME);

        return resourcePermissionAuthAction.hasResourcePermissionForCurrentUser(resourceName, ResourceTypeEnum.of(resourceType), ResourcePermissionTypeEnum.of(resourcePermissionType));
    }

    @BctMethodInfo(
            description = "modify a user's role",
            retName = "Resource",
            retDescription = "resource")
    @Transactional
    public UserDTO authUserRoleModify(
        @BctMethodArg(name = ApiParamConstants.USER_ID, description = "user id") String userId,
        @BctMethodArg(name = ApiParamConstants.ROLE_IDS, description = "role id") List<String> roleIds
    ) {

        CommonUtils.checkBlankParam(new HashMap<String,String>() {{
            put(ApiParamConstants.USER_ID, userId);
        }});

        if(CollectionUtils.isEmpty(roleIds))
            throw new AuthBlankParamException(ApiParamConstants.ROLE_IDS);

        return resourcePermissionAuthAction.modifyUserRole(userId, roleIds);
    }

}
