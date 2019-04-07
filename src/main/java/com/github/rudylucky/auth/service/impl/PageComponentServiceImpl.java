package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.business.PageComponentBusiness;
import com.github.rudylucky.auth.common.api.annotation.BctMethodArg;
import com.github.rudylucky.auth.common.api.annotation.BctMethodInfo;
import com.github.rudylucky.auth.dto.PageComponent;
import com.github.rudylucky.auth.common.exception.AuthBlankParamException;
import com.github.rudylucky.auth.service.ApiParamConstants;
import com.github.rudylucky.auth.service.PageComponentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Service
public class PageComponentServiceImpl implements PageComponentService {

    private PageComponentBusiness pageComponentBusiness;

    @Autowired
    public PageComponentServiceImpl(
            PageComponentBusiness pageComponentBusiness
    ) {
        this.pageComponentBusiness = pageComponentBusiness;
    }

    @BctMethodInfo
    public PageComponent authPageComponentList() {
        return pageComponentBusiness.authPageComponentList();
    }

    @BctMethodInfo
    public Boolean authPagePermissionSet(
            @BctMethodArg Collection<Map<String, Object>> permissions
    ) {
        if (CollectionUtils.isEmpty(permissions))
            throw new AuthBlankParamException(ApiParamConstants.PERMISSIONS);

        pageComponentBusiness.setPagePermission(permissions);
        return true;
    }

    @BctMethodInfo
    public void authPageComponentInitialize(
            @BctMethodArg Collection<Map<String, Object>> pages
    ) {
        if (CollectionUtils.isEmpty(pages))
            throw new AuthBlankParamException(ApiParamConstants.PAGES);

        pageComponentBusiness.initializePageComponent(pages);
    }

    @BctMethodInfo(description = "get page ids group by role name")
    public Set<Map<String, Object>> authPagePermissionGet() {
        return pageComponentBusiness.listPagePermission();
    }

    @BctMethodInfo(description = "get page ids group by roleId")
    public Collection<String> authPagePermissionGetByRoleId(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            throw new AuthBlankParamException(ApiParamConstants.ROLE_ID); }

        return pageComponentBusiness.listPagePermissionByRoleId(roleId);
    }
}
