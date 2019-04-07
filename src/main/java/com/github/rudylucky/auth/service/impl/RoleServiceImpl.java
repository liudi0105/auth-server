package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.authaction.RoleAuthAction;
import com.github.rudylucky.auth.common.api.annotation.BctMethodArg;
import com.github.rudylucky.auth.common.api.annotation.BctMethodInfo;
import com.github.rudylucky.auth.dto.RoleDTO;
import com.github.rudylucky.auth.service.ApiParamConstants;
import com.github.rudylucky.auth.service.RoleService;
import com.github.rudylucky.auth.common.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleAuthAction roleAuthAction;

    @Autowired
    public RoleServiceImpl(RoleAuthAction roleAuthAction){
        this.roleAuthAction = roleAuthAction;
    }

     @Override
    @BctMethodInfo(
            description = "Create RoleDbo",
            retName = "role",
            retDescription = "role")
    @Transactional
    public RoleDTO authRoleCreate(
            @BctMethodArg(name = ApiParamConstants.ROLE_NAME, description = "role name") String roleName,
            @BctMethodArg(name = ApiParamConstants.ALIAS, description = "role alias", required = false) String alias,
            @BctMethodArg(name = ApiParamConstants.REMARK, description = "remark", required = false) String remark) {

         CommonUtils.checkBlankParam(new HashMap<String, String>() {{
             put(ApiParamConstants.ROLE_NAME, roleName);
         }});

        return roleAuthAction.createRole(roleName, alias, remark);
    }

     @Override
    @BctMethodInfo(
            description = "Delete RoleDbo",
            retName = "role",
            retDescription = "role")
    @Transactional
    public Boolean authRoleRevoke(@BctMethodArg(name = ApiParamConstants.ROLE_ID, description = "role id") String roleId) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.ROLE_ID, roleId);
        }});

        roleAuthAction.deleteRole(roleId);
        return true;
    }

     @Override
    @BctMethodInfo(
            description = "Change RoleDbo",
            retName = "user",
            retDescription = "user"
    )
    @Transactional
    public RoleDTO authRoleUpdate(
            @BctMethodArg(name = ApiParamConstants.ROLE_ID, description = "role id") String roleId,
            @BctMethodArg(name = ApiParamConstants.ROLE_NAME, description = "role id") String roleName,
            @BctMethodArg(name = ApiParamConstants.ALIAS, description = "alias") String alias,
            @BctMethodArg(name = ApiParamConstants.REMARK, description = "remark") String remark) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.ROLE_ID, roleId);
            put(ApiParamConstants.ROLE_NAME, roleName);
        }});

        return roleAuthAction.updateRole(roleId, roleName, alias, remark);
    }

     @Override
    @BctMethodInfo(
            description = "Get All RoleDbo",
            retName = "roles",
            retDescription = "role list"
    )
    public Collection<RoleDTO> authRoleList() {
        return roleAuthAction.listRoles();
    }

     @Override
    @BctMethodInfo(
            description = "Get RoleDbo",
            retName = "role",
            retDescription = "role"
    )
    public RoleDTO authRoleGet(@BctMethodArg(name = ApiParamConstants.ROLE_ID, description = "role id") String roleId) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.ROLE_ID, roleId);
        }});

        return roleAuthAction.getRoleByRoleId(roleId);
    }

    /* @Override */
    @BctMethodInfo(
            description = "Get RoleDbo",
            retName = "role",
            retDescription = "role"
    )
    public RoleDTO authRoleGetByRoleName(@BctMethodArg(name = ApiParamConstants.ROLE_NAME, description = "role name") String roleName) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.ROLE_NAME, roleName);
        }});

        return roleAuthAction.getRoleByRoleName(roleName);
    }

}
