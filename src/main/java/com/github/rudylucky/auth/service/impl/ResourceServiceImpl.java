package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.authaction.ResourceAuthAction;
import com.github.rudylucky.auth.business.ResourceBusiness;
import com.github.rudylucky.auth.common.api.annotation.BctMethodArg;
import com.github.rudylucky.auth.common.api.annotation.BctMethodInfo;
import com.github.rudylucky.auth.common.exception.AuthServiceException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.common.util.CommonUtils;
import com.github.rudylucky.auth.dto.Resource;
import com.github.rudylucky.auth.dto.ResourceDTO;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;
import com.github.rudylucky.auth.service.ApiParamConstants;
import com.github.rudylucky.auth.service.ResourceService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;

@Service
public class ResourceServiceImpl implements ResourceService {

    private ResourceAuthAction resourceAuthAction;
    private ResourceBusiness resourceBusiness;

    @Autowired
    public ResourceServiceImpl(
            ResourceBusiness resourceBusiness,
            ResourceAuthAction resourceAuthAction) {
        this.resourceBusiness = resourceBusiness;
        this.resourceAuthAction = resourceAuthAction;
    }

     @Override
    @BctMethodInfo(
            description = "Add a resource to the hierarchy",
            retName = "result",
            retDescription = "Whether the addition succeeded or not")
    @Transactional
    public ResourceDTO authResourceCreate(
            @BctMethodArg(name = ApiParamConstants.RESOURCE_TYPE, description = "The type of the resource") String resourceType,
            @BctMethodArg(name = ApiParamConstants.RESOURCE_NAME, description = "The id of the resource") String resourceName,
            @BctMethodArg(name = ApiParamConstants.PARENT_ID, description = "The id of the parent resource") String parentId,
            @BctMethodArg(name = ApiParamConstants.SORT, description = "sort") Number sort) {

         CommonUtils.checkBlankParam(new HashMap<String, String>() {{
             put(ApiParamConstants.RESOURCE_TYPE, resourceType);
             put(ApiParamConstants.RESOURCE_NAME, resourceName);
             put(ApiParamConstants.PARENT_ID, parentId);
         }});

        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.of(resourceType);
        return resourceAuthAction.createResource(resourceName, resourceTypeEnum, parentId, sort.intValue());
    }

    @BctMethodInfo(
            description = "get resource tree",
            retName = "resource",
            retDescription = "resource")
    @Transactional
    public Resource authResourceGet(){
        return resourceAuthAction.getResource();
    }

    @BctMethodInfo(
            description = "get a resource tree of a user",
            retName = "resource",
            retDescription = "resource")
    @Transactional
    public Resource authResourceGetByUserId(
            @BctMethodArg(name = ApiParamConstants.USER_ID, description = "user id") String userId
    ) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USER_ID, userId);
        }});

        return resourceAuthAction.getResourceByUserId(userId);
    }

    @BctMethodInfo(
            description = "get a resource tree of a role",
            retName = "resource",
            retDescription = "resource")
    @Transactional
    public Resource authResourceGetByRoleId(
            @BctMethodArg(name = ApiParamConstants.ROLE_ID, description = "role id") String roleId
    ) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.ROLE_ID, roleId);
        }});

        return resourceAuthAction.getResourceByRoleId(roleId);
    }

     @Override
    @BctMethodInfo(
            description = "revoke a resource in the tree",
            retName = "result",
            retDescription = "the revoked resource")
    @Transactional
    public Resource authResourceRevoke(
            @BctMethodArg(name = ApiParamConstants.RESOURCE_ID, description = "The id of the resource") String resourceId) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.RESOURCE_ID, resourceId);
        }});

        return resourceAuthAction.deleteResource(resourceId);
    }

     @Override
    @BctMethodInfo(
            description = "modify the name of a resource",
            retName = "result",
            retDescription = "the resource modified")
    @Transactional
    public Resource authResourceModify(
            @BctMethodArg(name = ApiParamConstants.RESOURCE_ID, description = "The id of the resource") String resourceId,
            @BctMethodArg(name = ApiParamConstants.RESOURCE_NAME, description = "The id of the resource") String resourceName,
            @BctMethodArg(name = ApiParamConstants.PARENT_ID, description = "parentId") String parentId){

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.RESOURCE_ID, resourceId);
            put(ApiParamConstants.RESOURCE_NAME, resourceName);
            put(ApiParamConstants.PARENT_ID, parentId);
        }});

        return resourceBusiness.modifyResource(resourceId, resourceName, parentId);
    }

    @BctMethodInfo(
            description = "list all readable resource, but ",
            retName = "list of resource name",
            retDescription = "list of resource name"
    )
    @Transactional
    public Collection<String> authResourceList(
            @BctMethodArg(name = ApiParamConstants.RESOURCE_TYPE, description = "the type of resource") String resourceType){

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.RESOURCE_TYPE, resourceType);
        }});

        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.of(resourceType);
        if(!CollectionUtils.contains(Lists.newArrayList(ResourceTypeEnum.BOOK, ResourceTypeEnum.PORTFOLIO).iterator(), resourceTypeEnum))
            throw new AuthServiceException( ReturnMessageAndTemplateDef.Errors.LIST_RESOURCE_GROUP_ERROR);
        return resourceAuthAction.listResourceNameByResourceType(ResourceTypeEnum.of(resourceType));
    }

    @BctMethodInfo(
            description = "add NonGroup resource(book, portfolio)",
            retName = "resource dto",
            retDescription = "resource dto"
    )
    @Transactional
    public ResourceDTO authNonGroupResourceAdd(
            @BctMethodArg(name = ApiParamConstants.RESOURCE_TYPE, description = "the type of resource") String resourceType,
            @BctMethodArg(name = ApiParamConstants.RESOURCE_NAME, description = "name of resource") String resourceName){

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.RESOURCE_TYPE, resourceType);
            put(ApiParamConstants.RESOURCE_NAME, resourceName);
        }});

        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.of(resourceType);
        if(!CollectionUtils.contains(Lists.newArrayList(ResourceTypeEnum.BOOK, ResourceTypeEnum.PORTFOLIO).iterator(), resourceTypeEnum))
            throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.LIST_RESOURCE_GROUP_ERROR);

        return resourceAuthAction.createNonGroupResource(resourceName, resourceTypeEnum, 0);
    }

    @BctMethodInfo(
            description = "delete NonGroup resource(book, portfolio)",
            retName = "resource dto",
            retDescription = "resource dto"
    )
    @Transactional
    public Boolean authNonGroupResourceRevoke(
            @BctMethodArg(name = ApiParamConstants.RESOURCE_TYPE, description = "the type of resource") String resourceType,
            @BctMethodArg(name = ApiParamConstants.RESOURCE_NAME, description = "name of resource") String resourceName){

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.RESOURCE_TYPE, resourceType);
            put(ApiParamConstants.RESOURCE_NAME, resourceName);
        }});

        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.of(resourceType);
        if(!CollectionUtils.contains(Lists.newArrayList(ResourceTypeEnum.BOOK, ResourceTypeEnum.PORTFOLIO).iterator(), resourceTypeEnum))
            throw new AuthServiceException( ReturnMessageAndTemplateDef.Errors.DELETE_RESOURCE_GROUP_ERROR);
        resourceAuthAction.revokeResource(resourceName, resourceTypeEnum);
        return true;
    }

    @BctMethodInfo(
            description = "modify NonGroup resource(book, portfolio) name",
            retName = "resource dto",
            retDescription = "resource dto"
    )
    @Transactional
    public Boolean authNonGroupResourceModify(
            @BctMethodArg(name = ApiParamConstants.RESOURCE_NAME, description = "the name of resourceName") String resourceName,
            @BctMethodArg(name = ApiParamConstants.RESOURCE_TYPE, description = "the type of resource") String resourceType,
            @BctMethodArg(name = ApiParamConstants.NEW_RESOURCE_NAME, description = "the type of resource") String newResourceName) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.RESOURCE_NAME, resourceName);
            put(ApiParamConstants.RESOURCE_TYPE, resourceType);
            put(ApiParamConstants.NEW_RESOURCE_NAME, newResourceName);
        }});

        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.of(resourceType);
        if(!CollectionUtils.contains(Lists.newArrayList(ResourceTypeEnum.BOOK, ResourceTypeEnum.PORTFOLIO).iterator(), resourceTypeEnum))
            throw new AuthServiceException( ReturnMessageAndTemplateDef.Errors.MODIFY_RESOURCE_GROUP_ERROR);

        resourceAuthAction.modifyNonGroupResourceName(resourceName, resourceTypeEnum, newResourceName);
        return true;
    }

    @BctMethodInfo(description = "get all books the user can read")
    public Collection<ResourceDTO> authBookGetCanRead(
    ) {
        return resourceAuthAction.getReadableBook();
    }

}
