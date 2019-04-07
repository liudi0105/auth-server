package com.github.rudylucky.auth.authaction.impl;

import com.github.rudylucky.auth.authaction.ResourcePermissionAuthAction;
import com.github.rudylucky.auth.cache.ResourcePermissionCacheManager;
import com.github.rudylucky.auth.security.Constants;
import com.github.rudylucky.auth.dto.*;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;
import com.github.rudylucky.auth.common.exception.AuthServiceException;
import com.github.rudylucky.auth.common.exception.AuthorizationException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.manager.*;
import com.github.rudylucky.auth.common.util.ConverterUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ResourcePermissionAuthActionImpl implements ResourcePermissionAuthAction {
    private ResourcePermissionManager resourcePermissionManager;
    private UserManager userManager;
    private DepartmentManager departmentManager;
    private RoleResourcePermissionManager roleResourcePermissionManager;
    private ResourceManager resourceManager;
    private RoleManager roleManager;
    private ResourcePermissionCacheManager resourcePermissionCacheManager;

    @Autowired
    public ResourcePermissionAuthActionImpl(
            ResourcePermissionManager resourcePermissionManager
            , RoleResourcePermissionManager roleResourcePermissionManager
            , UserManager userManager
            , DepartmentManager departmentManager
            , ResourceManager resourceManager
            , ResourcePermissionCacheManager resourcePermissionCacheManager
            , RoleManager roleManager){
        this.roleResourcePermissionManager = roleResourcePermissionManager;
        this.resourcePermissionManager = resourcePermissionManager;
        this.resourceManager = resourceManager;
        this.departmentManager = departmentManager;
        this.userManager = userManager;
        this.resourcePermissionCacheManager = resourcePermissionCacheManager;
        this.roleManager = roleManager;
    }

    @Transactional
    public Boolean hasDepartmentResourcePermissionForCurrentUser(ResourcePermissionTypeEnum resourcePermissionType){
        UserDTO currentUserDto = userManager.getCurrentUser();
        DepartmentWithResourceDTO departmentWithResourceDto = departmentManager.getDepartmentWithResource(currentUserDto.getDepartmentId());
        return resourcePermissionCacheManager.cacheHasPermission(currentUserDto.getId(), departmentWithResourceDto.getResourceId(), resourcePermissionType);
    }

    @Transactional
    public Boolean hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum resourcePermissionType){
        UserDTO currentUserDto = userManager.getCurrentUser();
        DepartmentWithResourceDTO departmentWithResourceDto = departmentManager.getDepartmentWithResource(
                departmentManager.getDepartmentByDepartmentNameAndParentId(departmentManager.getCompanyInfo().getCompanyName(), null).getId());
        return resourcePermissionCacheManager.cacheHasPermission(currentUserDto.getId(), departmentWithResourceDto.getResourceId(), resourcePermissionType);
    }

    @Override
    @Transactional
    public Boolean hasDepartmentResourcePermissionForCurrentUser(String departmentId, ResourcePermissionTypeEnum resourcePermissionType) {
        UserDTO currentUserDto = userManager.getCurrentUser();
        DepartmentWithResourceDTO departmentWithResourceDto = departmentManager.getDepartmentWithResource(departmentId);
        return resourcePermissionCacheManager.cacheHasPermission(currentUserDto.getId(), departmentWithResourceDto.getResourceId(), resourcePermissionType);
    }

    @Transactional
    public Boolean hasResourcePermissionForCurrentUser(String resourceId, ResourcePermissionTypeEnum resourcePermissionType){
        UserDTO currentUserDto = userManager.getCurrentUser();
        return resourcePermissionCacheManager.cacheHasPermission(currentUserDto.getId(), resourceId, resourcePermissionType);
    }

    @Override
    @Transactional
    public UserDTO modifyUserRole(String userId, List<String> roleIds) {
        Department companyDepartment = departmentManager.getCompanyDepartment();
        if (!hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum.UPDATE_USER)) {
            String departmentName = companyDepartment.getDepartmentName();
            throw new AuthorizationException(ResourceTypeEnum.COMPANY, departmentName, ResourcePermissionTypeEnum.UPDATE_USER);
        }

        Collection<ResourcePermissionDTO> permissions = resourcePermissionManager.listResourcePermissionByUserId(userId);

        Collection<RoleResourcePermissionDTO> roleResourcePermissions = roleResourcePermissionManager.getRoleResourcePermissions(roleIds);
        UserDTO user = userManager.getUserByUserId(userId);
        if (Objects.equals(user.getUsername(), Constants.ADMIN)) {
            RoleDTO adminRole = roleManager.getValidRoleWithRoleName(Constants.ADMIN);
            if (roleResourcePermissions.stream().noneMatch(v -> Objects.equals(adminRole.getId(), v.getRoleId()))) {
                throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.FORBIDDEN_DELETE_ADMIN_ROLE_FOR_ADMIN);
            }
        }

        Set<ResourcePermissionDTO> toAddPermissions = roleResourcePermissions.stream()
                .map(v -> ConverterUtils.getResourcePermissionDto(userId, v)).collect(Collectors.toSet());

        List<String> oldRoleIds = userManager.findUserByUserId(userId).getRoleName().stream()
                .map(v -> Optional.ofNullable(roleManager.getValidRoleWithRoleName(v)).map(RoleDTO::getId)
                        .orElseThrow(() -> new AuthServiceException(ReturnMessageAndTemplateDef.Errors.MISSING_ROLE, v)))
                .filter(v -> !roleIds.contains(v))
                .collect(Collectors.toList());

        Set<ResourcePermissionDTO> toRemovePermissions = roleResourcePermissionManager.getRoleResourcePermissions(oldRoleIds).stream()
                .map(v -> ConverterUtils.getResourcePermissionDto(userId, v))
                .filter(v -> !toAddPermissions.contains(v))
                .collect(Collectors.toSet());

        permissions.removeAll(toRemovePermissions);
        permissions.addAll(toAddPermissions);

        String companyResourceId = departmentManager.getDepartmentWithResource(departmentManager.getCompanyDepartmentId().orElseThrow(
                () -> new AuthServiceException( ReturnMessageAndTemplateDef.Errors.MISSING_COMPANY_INFO)
        )).getResourceId();
        boolean userIsAdmin = Objects.equals(user.getUsername(), Constants.ADMIN);

        toRemovePermissions.removeAll(permissions);
        toRemovePermissions.stream().map(ResourcePermissionDTO::getResourceId)
                .forEach(v -> {
                    if (userIsAdmin && Objects.equals(v, companyResourceId)) {
                        throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.WEAKEN_PERMISIONS_OF_ADMIN);
                    }
                    resourcePermissionManager.modifyResourcePermissions(userId, v, Lists.newArrayList());
                });

        permissions.stream().collect(Collectors.groupingBy(ResourcePermissionDTO::getResourceId))
                .forEach((k, v) -> {
                    Set<ResourcePermissionTypeEnum> permissionTypeEnums = v.stream().map(ResourcePermissionDTO::getResourcePermission).collect(Collectors.toSet());
                    if (userIsAdmin && Objects.equals(k, companyResourceId)
                            && !permissionTypeEnums.containsAll(ResourcePermissionTypeEnum.Arrays.ADMIN_ON_COMPANY)) {

                        throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.WEAKEN_PERMISIONS_OF_ADMIN);
                    }

                    resourcePermissionManager.modifyResourcePermissions(userId, k, permissionTypeEnums);
                });

        return userManager.updateUserRoles(userId, roleIds);
    }

    @Override
    @Transactional
    public Collection<ResourcePermissionDTO> createResourcePermissions(String userId, String resourceId
            , Collection<ResourcePermissionTypeEnum> resourcePermissionTypeSet) {

        if(!hasResourcePermissionForCurrentUser(resourceId, ResourcePermissionTypeEnum.GRANT_ACTION)
                && !hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum.GRANT_ACTION)) {

            ResourceDTO resource = resourceManager.getResource(resourceId);
            throw new AuthorizationException(resource.getResourceType(), resource.getResourceName(), ResourcePermissionTypeEnum.GRANT_ACTION);
        }

        return resourcePermissionManager.createResourcePermissions(userId, resourceId, resourcePermissionTypeSet);
    }

    @Override
    @Transactional
    public Collection<ResourcePermissionDTO> modifyResourcePermissions(String userId, String resourceId, Collection<ResourcePermissionTypeEnum> resourcePermissionTypes) {

        ResourceDTO resource = resourceManager.getResource(resourceId);
        if(!hasResourcePermissionForCurrentUser(resourceId, ResourcePermissionTypeEnum.GRANT_ACTION)
                && !hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum.GRANT_ACTION)) {

            throw new AuthorizationException(resource.getResourceType(), resource.getResourceName(), ResourcePermissionTypeEnum.GRANT_ACTION);
        }

        String companyResourceId = departmentManager.getDepartmentWithResource(departmentManager.getCompanyDepartmentId().orElseThrow(
                () -> new AuthServiceException( ReturnMessageAndTemplateDef.Errors.MISSING_COMPANY_INFO)
        )).getResourceId();
        if (Objects.equals(userManager.getUserByUserName(Constants.ADMIN).getId(), userId) && Objects.equals(resourceId, companyResourceId)
                && !resourcePermissionTypes.containsAll(ResourcePermissionTypeEnum.Arrays.ADMIN_ON_COMPANY)) {

            throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.WEAKEN_PERMISIONS_OF_ADMIN);
        }

        return resourcePermissionManager.modifyResourcePermissions(userId, resourceId, resourcePermissionTypes);

    }

    @Override
    @Transactional
    public List<Boolean> hasResourcePermissionForCurrentUser(String resourceName, ResourceTypeEnum resourceType, List<ResourcePermissionTypeEnum> resourcePermissionTypeList) {
        UserDTO currentUserDto = userManager.getCurrentUser();
        ResourceDTO resourceDto = resourceManager.getResource(resourceName, resourceType);
        return resourcePermissionManager.hasPermission(currentUserDto.getId(), resourceDto.getId(), resourcePermissionTypeList);
    }

    @Override
    @Transactional
    public List<Boolean> hasResourcePermissionForCurrentUser(List<String> resourceName, ResourceTypeEnum resourceTypeEnum, ResourcePermissionTypeEnum resourcePermissionType) {
        UserDTO currentUserDto = userManager.getCurrentUser();
        List<String> resourceIdList = resourceManager.listResource(resourceName, resourceTypeEnum)
                .stream()
                .map(resourceDto -> {
                    if(Objects.isNull(resourceDto)) return null;
                    else return resourceDto.getId();
                })
                .collect(Collectors.toList());
        return resourcePermissionManager.hasPermission(currentUserDto.getId(), resourceIdList, resourcePermissionType);
    }
}
