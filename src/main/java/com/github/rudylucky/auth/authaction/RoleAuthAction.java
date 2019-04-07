package com.github.rudylucky.auth.authaction;


import com.github.rudylucky.auth.dto.RoleDTO;

import java.util.Collection;

public interface RoleAuthAction {

    RoleDTO createRole(String roleName, String alias, String remark);

    void deleteRole(String roleId);

    RoleDTO updateRole(String roleId, String roleName, String alias, String remark);

    Collection<RoleDTO> listRoles();

    RoleDTO getRoleByRoleId(String roleId);

    RoleDTO getRoleByRoleName(String roleName);
}
