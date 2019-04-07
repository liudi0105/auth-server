package com.github.rudylucky.auth.manager;


import com.github.rudylucky.auth.common.exception.ManagerException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.common.util.tree.TreeEntity;
import com.github.rudylucky.auth.dao.PageComponentRepo;
import com.github.rudylucky.auth.dao.PagePermissionRepo;
import com.github.rudylucky.auth.dao.entity.PageComponentDbo;
import com.github.rudylucky.auth.dao.entity.PagePermissionDbo;
import com.github.rudylucky.auth.dto.PageComponent;
import com.github.rudylucky.auth.dto.PageComponentDTO;
import com.github.rudylucky.auth.dto.PagePermissionDTO;
import com.github.rudylucky.auth.common.util.ConverterUtils;
import com.github.rudylucky.auth.service.ApiParamConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PageComponentManager {

    private PagePermissionRepo pagePermissionRepo;
    private PageComponentRepo pageComponentRepo;

    @Autowired
    public PageComponentManager(
            PageComponentRepo pageComponentRepo,
            PagePermissionRepo pagePermissionRepo
    ){
        this.pageComponentRepo = pageComponentRepo;
        this.pagePermissionRepo = pagePermissionRepo;
    }

    public PageComponent getPageTreeByRoleNames(Collection<String> roleId) {

        List<String> pageIds = pagePermissionRepo.findValidPagePermissionByRoleIds(roleId).stream()
                .map(PagePermissionDbo::getPageComponentId).collect(Collectors.toList());

        return TreeEntity.fromRecords(pageComponentRepo.findValidPageComponentByIds(pageIds), (pageComponentDbo, parent) -> {
            PageComponentDbo dbo = (PageComponentDbo) pageComponentDbo;
            return new PageComponent(dbo.getId(), dbo.getSort(), parent, dbo.getPageName());
        });
    }

    public PageComponent authPageComponentList() {
        return TreeEntity.fromRecords(pageComponentRepo.findValidPageComponent(), (pageComponentDbo, parent) -> {
            PageComponentDbo dbo = (PageComponentDbo) pageComponentDbo;
            return new PageComponent(dbo.getId(), dbo.getSort(), parent, dbo.getPageName());
        });
    }

    @Transactional
    public void setPagePermissions(String roleId, Collection<String> pageComponentIds){
        if (CollectionUtils.isEmpty(pageComponentIds))
            return;
        // Clear the role page permissions to reset
        pagePermissionRepo.deleteValidPagePermissionByRoleId(roleId);

        Collection<PagePermissionDTO> pagePermissionDTOs = new HashSet<>();

        pageComponentIds.forEach(p -> setPagePermissionsParent(pagePermissionDTOs,roleId,p));

        pagePermissionRepo.saveAll(pagePermissionDTOs.stream().map(ConverterUtils::getPagePermissionDbo).collect(Collectors.toSet()));
    }

    public void setPagePermissionsParent(Collection<PagePermissionDTO> pagePermissionDTOs, String roleId, String pageComponentId){
        if(pagePermissionRepo.findValidPagePermissionByRoleIdAndPageComponentId(roleId, pageComponentId).isPresent())
            return;
        pagePermissionDTOs.add(new PagePermissionDTO(roleId,pageComponentId));
        Optional<PageComponentDbo> pageComponentDbo = pageComponentRepo.findValidPageComponentById(pageComponentId);
        String parentId = pageComponentDbo.get().getParentId();
        if(Objects.isNull(parentId))
            return;
        setPagePermissionsParent(pagePermissionDTOs,roleId,parentId);
    }


    @Transactional
    public void createPages(Collection<PageComponentDTO> pageComponentDtos) {
        pageComponentRepo.saveAll(pageComponentDtos.stream()
                .map(ConverterUtils::getPageComponentDbo).collect(Collectors.toSet()));
    }

    @Transactional
    public void initializePages(String parentId, Map<String, Object> page) {
        String pageName = (String) page.get(ApiParamConstants.PAGE_NAME);
        Integer sort = (Integer) page.get(ApiParamConstants.SORT);
        Optional<PageComponentDbo> pageOptional = pageComponentRepo.findValidPageComponentByPageName(pageName);
        if(!pageOptional.isPresent()) {
            PageComponentDTO toSavePageComponentDTO = new PageComponentDTO(null, pageName, sort, parentId);
            pageComponentRepo.save(ConverterUtils.getPageComponentDbo(toSavePageComponentDTO));
        }
        parentId = pageComponentRepo.findValidPageComponentByPageName(pageName).orElseThrow(
                () -> new ManagerException(ReturnMessageAndTemplateDef.Errors.MISSING_PAGE_COMPONENT, pageName)).getId();
        List<Map<String, Object>> children = (List<Map<String, Object>>) page.get(ApiParamConstants.CHILDREN);
        if(CollectionUtils.isEmpty(children))
            return;
        String finalParentId = parentId;
        children.forEach(c -> initializePages(finalParentId,c));
    }

    public Set<Map<String, Object>> listPagePermission() {
        return pagePermissionRepo.findAllValidPagePermission().stream().collect(Collectors.groupingBy(PagePermissionDbo::getRoleId))
                .entrySet().stream().map(p -> new HashMap<String, Object>() {{
                    put(ApiParamConstants.ROLE_ID, p.getKey());
                    put(ApiParamConstants.PAGE_COMPONENT_ID, p.getValue().stream().map(PagePermissionDbo::getPageComponentId).collect(Collectors.toSet()));
                }}).collect(Collectors.toSet());
    }

    public String getPageComponentIdByPageName(String pageName) {
         return pageComponentRepo.findValidPageComponentByPageName(pageName).orElseThrow(
                () -> new ManagerException(ReturnMessageAndTemplateDef.Errors.MISSING_PAGE_COMPONENT, pageName)).getId();
    }

    public Collection<PagePermissionDTO> listPagePermissionByRoleId(String roleId){
        return pagePermissionRepo.findValidPagePermissionByRoleId(roleId).stream()
                .map(ConverterUtils::getPagePermissionDTO)
                .collect(Collectors.toSet());
    }

}
