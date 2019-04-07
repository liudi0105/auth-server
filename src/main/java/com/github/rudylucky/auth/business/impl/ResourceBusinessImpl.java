package com.github.rudylucky.auth.business.impl;

import com.github.rudylucky.auth.authaction.DepartmentAuthAction;
import com.github.rudylucky.auth.authaction.ResourceAuthAction;
import com.github.rudylucky.auth.business.ResourceBusiness;
import com.github.rudylucky.auth.dto.Department;
import com.github.rudylucky.auth.dto.Resource;
import com.github.rudylucky.auth.dto.ResourceDTO;
import com.github.rudylucky.auth.manager.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ResourceBusinessImpl implements ResourceBusiness {

    private ResourceAuthAction resourceAuthAction;
    private DepartmentAuthAction departmentAuthAction;
    private ResourceManager resourceManager;

    @Autowired
    public ResourceBusinessImpl(
            ResourceManager resourceManager,
            DepartmentAuthAction departmentAuthAction,
            ResourceAuthAction resourceAuthAction
    ) {
        this.resourceManager = resourceManager;
        this.resourceAuthAction = resourceAuthAction;
        this.departmentAuthAction = departmentAuthAction;
    }

    @Override
    @Transactional
    public Resource modifyResource(String resourceId, String resourceName, String parentId) {
        ResourceDTO resource = resourceManager.getResource(resourceId);
        String departmentId = resource.getDepartmentId();
        if (Objects.nonNull(departmentId)) {
            Department department = departmentAuthAction.getDepartmentById(departmentId);
            departmentAuthAction.updateDepartment(department.getId(), resourceName, department.getDepartmentType(), department.getDescription());

            String newParentDepartmentId = resourceManager.getResource(parentId).getDepartmentId();
            departmentAuthAction.moveDepartment(departmentId, newParentDepartmentId);
        }

        resourceAuthAction.modifyResource(resourceId, resourceName);
        return resourceAuthAction.moveResource(resourceId, parentId);
    }
}
