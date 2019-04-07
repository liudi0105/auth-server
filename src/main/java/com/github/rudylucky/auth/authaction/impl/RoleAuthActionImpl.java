package com.github.rudylucky.auth.authaction.impl;

import com.github.rudylucky.auth.authaction.ResourcePermissionAuthAction;
import com.github.rudylucky.auth.authaction.RoleAuthAction;
import com.github.rudylucky.auth.common.AuthConstants;
import com.github.rudylucky.auth.dto.RoleDTO;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;
import com.github.rudylucky.auth.common.exception.AuthServiceException;
import com.github.rudylucky.auth.common.exception.AuthorizationException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.manager.DepartmentManager;
import com.github.rudylucky.auth.manager.RoleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class RoleAuthActionImpl implements RoleAuthAction {

    private RoleManager roleManager;
    private ResourcePermissionAuthAction resourcePermissionAuthAction;
    private DepartmentManager departmentManager;

    @Autowired
    public RoleAuthActionImpl(
            RoleManager roleManager
            , DepartmentManager departmentManager
            , ResourcePermissionAuthAction resourcePermissionAuthAction){
        this.departmentManager = departmentManager;
        this.roleManager = roleManager;
        this.resourcePermissionAuthAction = resourcePermissionAuthAction;
    }

    @Override
    public RoleDTO createRole(String roleName, String alias, String remark) {
        if(!resourcePermissionAuthAction.hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum.CREATE_ROLE)) {
            String companyName = departmentManager.getCompanyInfo().getCompanyName();
            throw new AuthorizationException(ResourceTypeEnum.COMPANY, companyName, ResourcePermissionTypeEnum.CREATE_ROLE);
        }

        if (roleManager.isRoleExist(roleName)) {
            throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.DUPLICATE_ROLE_NAME, roleName);
        }
        return roleManager.createRole(roleName, alias, remark);
    }

    @Override
    public void deleteRole(String roleId) {
        if(!resourcePermissionAuthAction.hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum.DELETE_ROLE)) {
            String companyName = departmentManager.getCompanyInfo().getCompanyName();
            throw new AuthorizationException(ResourceTypeEnum.COMPANY, companyName, ResourcePermissionTypeEnum.DELETE_ROLE);
        }

        if (roleManager.getValidRoleWithRoleId(roleId).getRoleName().equals(AuthConstants.ADMIN))
            throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.DELETE_ADMIN_ROLE);

        roleManager.removeRole(roleId);
    }

    @Override
    public RoleDTO updateRole(String roleId, String roleName, String alias, String remark) {
        if(!resourcePermissionAuthAction.hasCompanyResourcePermissionForCurrentUser(ResourcePermissionTypeEnum.UPDATE_ROLE)) {
            String companyName = departmentManager.getCompanyInfo().getCompanyName();
            throw new AuthorizationException(ResourceTypeEnum.COMPANY, companyName, ResourcePermissionTypeEnum.UPDATE_ROLE);
        }

        if (roleManager.getValidRoleWithRoleId(roleId).getRoleName().equals(AuthConstants.ADMIN)
                && !AuthConstants.ADMIN.equals(roleName)) {

            throw new AuthServiceException(ReturnMessageAndTemplateDef.Errors.UPDATE_ADMIN_ROLE_NAME);
        }

        return roleManager.updateRole(roleId, roleName, alias, remark);
    }

    @Override
    public Collection<RoleDTO> listRoles() {
        return roleManager.listAllValidRoles();
    }

    @Override
    public RoleDTO getRoleByRoleId(String roleId) {
        return roleManager.getValidRoleWithRoleId(roleId);
    }

    @Override
    public RoleDTO getRoleByRoleName(String roleName) {
        return roleManager.getValidRoleWithRoleName(roleName);
    }
}
