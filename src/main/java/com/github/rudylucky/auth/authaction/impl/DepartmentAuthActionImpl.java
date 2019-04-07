package com.github.rudylucky.auth.authaction.impl;

import com.github.rudylucky.auth.authaction.DepartmentAuthAction;
import com.github.rudylucky.auth.authaction.ResourceAuthAction;
import com.github.rudylucky.auth.authaction.ResourcePermissionAuthAction;
import com.github.rudylucky.auth.common.util.tree.TreeEntity;
import com.github.rudylucky.auth.dao.entity.EntityConstants;
import com.github.rudylucky.auth.dto.*;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;
import com.github.rudylucky.auth.common.exception.AuthServiceException;
import com.github.rudylucky.auth.common.exception.AuthorizationException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.manager.DepartmentManager;
import com.github.rudylucky.auth.manager.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Component
public class DepartmentAuthActionImpl implements DepartmentAuthAction {

    private DepartmentManager departmentManager;
    private ResourcePermissionAuthAction resourcePermissionAuthAction;
    private ResourceAuthAction resourceAuthAction;
    private ResourceManager resourceManager;

    @Autowired
    public DepartmentAuthActionImpl(
            DepartmentManager departmentManager
            , ResourceManager resourceManager
            , ResourceAuthAction resourceAuthAction
            , ResourcePermissionAuthAction resourcePermissionAuthAction){
        this.resourceManager = resourceManager;
        this.departmentManager = departmentManager;
        this.resourcePermissionAuthAction = resourcePermissionAuthAction;
        this.resourceAuthAction = resourceAuthAction;
    }

    @Override
    @Transactional
    public DepartmentDTO createDepartment(String departmentName, String departmentType, String description, String parentId, Integer sort) {
        if(!resourcePermissionAuthAction.hasDepartmentResourcePermissionForCurrentUser(parentId, ResourcePermissionTypeEnum.CREATE_DEPARTMENT)) {
            DepartmentDTO department = departmentManager.getDepartmentDTO(parentId);
            throw new AuthorizationException(ResourceTypeEnum.DEPT, department.getDepartmentName(), ResourcePermissionTypeEnum.CREATE_DEPARTMENT);
        }

        if (Optional.ofNullable(getDepartmentById(parentId)).map(TreeEntity::getChildren)
                .map(c -> c.stream().anyMatch(v -> Objects.equals(v.getDepartmentName(), departmentName))).orElse(false))

            throw new AuthServiceException(
                    ReturnMessageAndTemplateDef.Errors.EXISTING_SAME_NAME_DEPARTMENT,
                    EntityConstants.DEPARTMENT_NAME, departmentName
            );

        return departmentManager.createDepartment(departmentName, departmentType, description, parentId, sort);
    }

    @Override
    public Department getDepartmentById(String departmentId) {
        return departmentManager.getDepartment(departmentId);
    }

    @Override
    public Department moveDepartment(String departmentId, String parentId) {
        DepartmentDTO department = departmentManager.getDepartmentDTO(departmentId);
        String oldParentId = department.getParentId();

        if (Objects.equals(oldParentId, parentId)) {
            return departmentManager.getCompanyDepartment();
        }

        if (!resourcePermissionAuthAction.hasDepartmentResourcePermissionForCurrentUser(oldParentId, ResourcePermissionTypeEnum.DELETE_DEPARTMENT)) {
            DepartmentDTO oldParent = departmentManager.getDepartmentDTO(oldParentId);
            throw new AuthorizationException(ResourceTypeEnum.DEPT, oldParent.getDepartmentName(), ResourcePermissionTypeEnum.DELETE_DEPARTMENT);
        }

        DepartmentWithResourceDTO parentDepartmentWithResource = departmentManager.getDepartmentWithResource(parentId);
        if (!resourcePermissionAuthAction.hasDepartmentResourcePermissionForCurrentUser(parentId, ResourcePermissionTypeEnum.CREATE_DEPARTMENT)) {
            throw new AuthorizationException(ResourceTypeEnum.DEPT, parentDepartmentWithResource.getDepartmentName(), ResourcePermissionTypeEnum.CREATE_DEPARTMENT);
        }

        DepartmentWithResourceDTO departmentWithResource = departmentManager.getDepartmentWithResource(departmentId);
        if (!resourcePermissionAuthAction.hasResourcePermissionForCurrentUser(departmentWithResource.getResourceParentId(), ResourcePermissionTypeEnum.DELETE_NAMESPACE)) {
            ResourceDTO resource = resourceManager.getResource(departmentWithResource.getResourceParentId());
            throw new AuthorizationException(resource.getResourceType(), resource.getResourceName(), ResourcePermissionTypeEnum.DELETE_NAMESPACE);
        }

        if (!resourcePermissionAuthAction.hasResourcePermissionForCurrentUser(parentDepartmentWithResource.getResourceId(), ResourcePermissionTypeEnum.CREATE_NAMESPACE)) {
            ResourceDTO resource = resourceManager.getResource(parentDepartmentWithResource.getResourceId());
            throw new AuthorizationException(resource.getResourceType(), resource.getResourceName(), ResourcePermissionTypeEnum.CREATE_NAMESPACE);
        }

        if (Optional.ofNullable(getDepartmentById(parentId).getChildren()).map(depts -> depts.stream()
                .anyMatch(dept -> Objects.equals(dept.getDepartmentName(), department.getDepartmentName()))).orElse(false)) {

            throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.EXISTING_SAME_NAME_DEPARTMENT,
                    parentDepartmentWithResource.getDepartmentName(), department.getDepartmentName());
        }

        departmentManager.moveDepartment(departmentId, parentId);
        return departmentManager.getCompanyDepartment();
    }

    @Override
    public Department updateDepartment(String departmentId, String departmentName, String departmentType, String description) {
        DepartmentWithResourceDTO departmentWithResourceDto = departmentManager.getDepartmentWithResource(departmentId);
        if(!resourcePermissionAuthAction.hasDepartmentResourcePermissionForCurrentUser(departmentId, ResourcePermissionTypeEnum.UPDATE_DEPARTMENT)) {
            throw new AuthorizationException(ResourceTypeEnum.DEPT, departmentWithResourceDto.getDepartmentName(), ResourcePermissionTypeEnum.UPDATE_DEPARTMENT);
        }

        if (!resourcePermissionAuthAction.hasResourcePermissionForCurrentUser(departmentWithResourceDto.getResourceId(), ResourcePermissionTypeEnum.UPDATE_NAMESPACE)) {
            ResourceDTO resource = resourceManager.getResource(departmentWithResourceDto.getResourceId());
            throw new AuthorizationException(resource.getResourceType(), resource.getResourceName(), ResourcePermissionTypeEnum.UPDATE_NAMESPACE);
        }

        if (Objects.nonNull(departmentWithResourceDto.getParentId()) && !Objects.equals(departmentWithResourceDto.getResourceName(), departmentName)
                && getDepartmentById(departmentWithResourceDto.getParentId()).getChildren()
                .stream().anyMatch(v -> Objects.equals(v.getDepartmentName(), departmentName))) {

            throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.EXISTING_SAME_NAME_DEPARTMENT,
                    departmentWithResourceDto.getParentId(), departmentWithResourceDto.getDepartmentName());
        }

        departmentManager.updateDepartment(departmentId, departmentName, departmentType, description);
        resourceAuthAction.modifyResource(departmentWithResourceDto.getResourceId(), departmentName);

        if (Objects.isNull(departmentWithResourceDto.getParentId())) {
            CompanyInfo companyInfo = departmentManager.getCompanyInfo();
            departmentManager.updateCompanyInfo(
                    departmentName,
                    departmentType,
                    companyInfo.getUnifiedSocialCreditCode(),
                    companyInfo.getLegalPerson(),
                    companyInfo.getContactEmail(),
                    companyInfo.getDescription()
            );
        }
        return departmentManager.getCompanyDepartment();
    }
}
