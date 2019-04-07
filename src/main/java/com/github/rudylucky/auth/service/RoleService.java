package com.github.rudylucky.auth.service;


import com.github.rudylucky.auth.dto.RoleDTO;

import java.util.Collection;

public interface RoleService {

    RoleDTO authRoleCreate(String roleName, String alias, String remark);

    Boolean authRoleRevoke(String roleId);

    RoleDTO authRoleUpdate(String roleId, String roleName, String alias, String remark);

    Collection<RoleDTO> authRoleList();

    RoleDTO authRoleGet(String roleId);

    RoleDTO authRoleGetByRoleName(String roleName);
}
