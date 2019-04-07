package com.github.rudylucky.auth.business;


import com.github.rudylucky.auth.dto.PageComponent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface PageComponentBusiness {

    PageComponent authPageComponentList();

    void setPagePermission(Collection<Map<String, Object>> permissions);

    void initializePageComponent(Collection<Map<String, Object>> pages);

    Set<Map<String, Object>> listPagePermission();

    Collection<String> listPagePermissionByRoleId(String roleId);
}
