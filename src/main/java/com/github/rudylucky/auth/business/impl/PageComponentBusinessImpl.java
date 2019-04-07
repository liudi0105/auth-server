package com.github.rudylucky.auth.business.impl;

import com.github.rudylucky.auth.business.PageComponentBusiness;
import com.github.rudylucky.auth.dto.PageComponent;
import com.github.rudylucky.auth.dto.PageComponentDTO;
import com.github.rudylucky.auth.dto.PagePermissionDTO;
import com.github.rudylucky.auth.manager.PageComponentManager;
import com.github.rudylucky.auth.manager.RoleManager;
import com.github.rudylucky.auth.service.ApiParamConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PageComponentBusinessImpl implements PageComponentBusiness {

    private PageComponentManager pageComponentManager;
    private RoleManager roleManager;

    @Autowired
    public PageComponentBusinessImpl(
            PageComponentManager pageComponentManager,
            RoleManager roleManager
    ) {
        this.pageComponentManager = pageComponentManager;
        this.roleManager = roleManager;
    }

    @Override
    public PageComponent authPageComponentList() {
        return pageComponentManager.authPageComponentList();
    }

    @Override
    public void setPagePermission(Collection<Map<String, Object>> permissions) {
        permissions.forEach(permission -> {
            String roleId = (String) permission.get(ApiParamConstants.ROLE_ID);
            Collection<String> pageComponentId = (Collection<String>) permission.get(ApiParamConstants.PAGE_COMPONENT_ID);
            pageComponentManager.setPagePermissions(roleId, pageComponentId);
        });
    }

    @Override
    public void initializePageComponent(Collection<Map<String, Object>> pages) {
        Set<PageComponentDTO> pageComponentDtos = pages.stream().map(page -> {
            String pageName = (String) page.get(ApiParamConstants.PAGE_NAME);
            Integer sort = (Integer) page.get(ApiParamConstants.SORT);
            String parentId = (String) page.get(ApiParamConstants.PARENT_ID);

            return new PageComponentDTO(null, pageName, sort, parentId);
        }).collect(Collectors.toSet());

        pageComponentManager.createPages(pageComponentDtos);

    }

    @Override
    public Set<Map<String, Object>> listPagePermission() {
        return pageComponentManager.listPagePermission();
    }

    @Override
    public Collection<String> listPagePermissionByRoleId(String roleId) {
        return pageComponentManager.listPagePermissionByRoleId(roleId)
                .stream()
                .map(PagePermissionDTO::getPageComponentId)
                .collect(Collectors.toList());
    }
}
