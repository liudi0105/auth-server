package com.github.rudylucky.auth.initialize;

import com.github.rudylucky.auth.common.AuthConstants;
import com.github.rudylucky.auth.common.util.FileUtils;
import com.github.rudylucky.auth.common.util.JsonUtils;
import com.github.rudylucky.auth.common.util.SystemConfig;
import com.github.rudylucky.auth.dto.DepartmentDTO;
import com.github.rudylucky.auth.dto.ResourceDTO;
import com.github.rudylucky.auth.dto.RoleDTO;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.enums.ResourceTypeEnum;
import com.github.rudylucky.auth.enums.UserTypeEnum;
import com.github.rudylucky.auth.manager.*;
import com.github.rudylucky.auth.service.impl.PageComponentServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class AuthServiceInitializer {
    
    private UserManager userManager;
    private RoleManager roleManager;
    private ResourceManager resourceManager;
    private ResourcePermissionManager resourcePermissionManager;
    private DepartmentManager departmentManager;
    private PageComponentManager pageComponentManager;
    private PageComponentServiceImpl pageComponentService;

    @Autowired
    public AuthServiceInitializer(
            UserManager userManager
            , RoleManager roleManager
            , ResourceManager resourceManager
            , ResourcePermissionManager resourcePermissionManager
            , DepartmentManager departmentManager
            , PageComponentManager pageComponentManager
            , PageComponentServiceImpl pageComponentService){
        this.userManager = userManager;
        this.roleManager = roleManager;
        this.resourceManager = resourceManager;
        this.resourcePermissionManager = resourcePermissionManager;
        this.departmentManager = departmentManager;
        this.pageComponentManager = pageComponentManager;
        this.pageComponentService = pageComponentService;
    }

    @Bean
    CommandLineRunner systemInitialize(){
        return (args) -> {
            SystemConfig.put(AuthConstants.EXPIRATION_DAYS, 30);
            SystemConfig.put(AuthConstants.MAX_LOGIN_FAILURE_TIMES, 8);
        };
    }

    @Order(1)
    @Bean
    @Transactional
    CommandLineRunner dbInitialize(){
        return (args) -> {
            String companyName = "上海同余信息科技有限公司";
            if(!departmentManager.hasCompanyInfo())
                departmentManager.createCompanyInfo(companyName, "金融科技", "xxxxxxxxxx"
                    , "金斌", "jinbin@tongyu.tech", companyName);
            DepartmentDTO departmentDto;

            List<String> roleIdList = Lists.newArrayList();
            if(!roleManager.isRoleExist(AuthConstants.ADMIN)) {
                RoleDTO roleDto = roleManager.createRole(AuthConstants.ADMIN, AuthConstants.ADMIN, AuthConstants.ADMIN);
                roleIdList.add(roleDto.getId());
            }

            if(!departmentManager.hasRootDepartment()) {
                 departmentDto = departmentManager.createDepartment(companyName, "公司"
                         , companyName, null, 0);
            }else {
                 departmentDto = departmentManager.getDepartmentByDepartmentNameAndParentId(companyName, null);
            }

            if(!userManager.isUserExists(AuthConstants.ADMIN)) {
                UserDTO userDto = userManager.createUser(AuthConstants.ADMIN, AuthConstants.ADMIN, "yangyiwei@tongyu.tech", "12345"
                        , UserTypeEnum.NORMAL, departmentDto.getId());
                userManager.updateUserRoles(userDto.getId(), roleIdList);
            }

            if(!resourceManager.isRootResourceExist()){
                ResourceDTO resourceDto = resourceManager.createRootResource(companyName, departmentDto.getId());
                departmentManager.linkDepartmentAndResource(departmentDto.getId(), resourceDto.getId());
                resourcePermissionManager.createResourcePermissions(AuthConstants.ADMIN, companyName, ResourceTypeEnum.NAMESPACE, null
                        , Lists.newArrayList(
                                ResourcePermissionTypeEnum.CREATE_NAMESPACE
                                , ResourcePermissionTypeEnum.DELETE_NAMESPACE
                                , ResourcePermissionTypeEnum.UPDATE_NAMESPACE
                                , ResourcePermissionTypeEnum.READ_RESOURCE
                                , ResourcePermissionTypeEnum.GRANT_ACTION
                                , ResourcePermissionTypeEnum.CREATE_USER
                                , ResourcePermissionTypeEnum.READ_USER
                                , ResourcePermissionTypeEnum.UPDATE_USER
                                , ResourcePermissionTypeEnum.DELETE_USER
                                , ResourcePermissionTypeEnum.CREATE_INSTRUMENT
                                , ResourcePermissionTypeEnum.UPDATE_INSTRUMENT
                                , ResourcePermissionTypeEnum.DELETE_INSTRUMENT
                                , ResourcePermissionTypeEnum.CREATE_DEPARTMENT
                                , ResourcePermissionTypeEnum.UPDATE_DEPARTMENT
                                , ResourcePermissionTypeEnum.DELETE_DEPARTMENT
                                , ResourcePermissionTypeEnum.LOCK_USER
                                , ResourcePermissionTypeEnum.UNLOCK_USER
                                , ResourcePermissionTypeEnum.EXPIRE_USER
                                , ResourcePermissionTypeEnum.UNEXPIRE_USER
                                , ResourcePermissionTypeEnum.CHANGE_PASSWORD
                                , ResourcePermissionTypeEnum.CREATE_ROLE
                                , ResourcePermissionTypeEnum.DELETE_ROLE
                                , ResourcePermissionTypeEnum.UPDATE_ROLE
                        ));
            }

            String marginName = "保证金";
            ResourceDTO rootResource = resourceManager.getRootResource();
            if (!resourceManager.isResourceExist(marginName, ResourceTypeEnum.MARGIN, rootResource.getId())) {
                resourceManager.createResource(marginName, ResourceTypeEnum.MARGIN, rootResource.getId(), departmentDto.getId(), 0);
                resourcePermissionManager.createResourcePermissions(AuthConstants.ADMIN, marginName, ResourceTypeEnum.MARGIN, rootResource.getId(),
                        Lists.newArrayList(
                                ResourcePermissionTypeEnum.GRANT_ACTION
                        ));
            }

            String clientInfoName = "客户信息";
            if (!resourceManager.isResourceExist(clientInfoName, ResourceTypeEnum.CLIENT_INFO, rootResource.getId())) {
                resourceManager.createResource(clientInfoName, ResourceTypeEnum.CLIENT_INFO, rootResource.getId(), departmentDto.getId(), 0);
                resourcePermissionManager.createResourcePermissions(AuthConstants.ADMIN, clientInfoName, ResourceTypeEnum.CLIENT_INFO, rootResource.getId()
                        , Lists.newArrayList(
                                ResourcePermissionTypeEnum.GRANT_ACTION
                        )
                );
            }
        };
    }

    @Order(5)
    @Bean
    @Transactional
    CommandLineRunner pageInit(){
        return (args) -> {
            // read pages.json
            Map<String,Object> map = JsonUtils.fromJson(FileUtils.readClassPathFile("pages.json"));
            // save data
            pageComponentManager.initializePages(null,map);
        };
    }

    @Order(6)
    @Bean
    @Transactional
    CommandLineRunner pagePermission(){
        return (args -> {
            String adminRoleId = roleManager.getRoleByRoleName("admin");
            if(!CollectionUtils.isEmpty(pageComponentManager.listPagePermissionByRoleId(adminRoleId)))
                return;
            Collection<String> pageComponentIds = new HashSet<>();
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("systemSettings"));
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("calendars"));
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("riskSettings"));
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("roleManagement"));
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("users"));
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("department"));
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("resources"));
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("tradeBooks"));
            pageComponentIds.add(pageComponentManager.getPageComponentIdByPageName("volatilityCalendar"));
            pageComponentManager.setPagePermissions(adminRoleId,pageComponentIds);
        });
    }

}
