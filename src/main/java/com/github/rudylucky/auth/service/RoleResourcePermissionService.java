package com.github.rudylucky.auth.service;


import com.github.rudylucky.auth.dto.Resource;

import java.util.List;
import java.util.Map;

public interface RoleResourcePermissionService {

    Resource authRolePermissionsModify(String roleId, List<Map<String, Object>> permissions);
}
