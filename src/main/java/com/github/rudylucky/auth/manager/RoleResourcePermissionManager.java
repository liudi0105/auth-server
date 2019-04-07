package com.github.rudylucky.auth.manager;

import com.github.rudylucky.auth.dao.RoleResourcePermissionRepo;
import com.github.rudylucky.auth.dao.entity.RoleResourcePermissionDbo;
import com.github.rudylucky.auth.dto.RoleResourcePermissionDTO;
import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.common.util.ConverterUtils;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RoleResourcePermissionManager {

    private RoleResourcePermissionRepo roleResourcePermissionRepo;

    @Autowired
    public RoleResourcePermissionManager(
            RoleResourcePermissionRepo roleResourcePermissionRepo){
        this.roleResourcePermissionRepo = roleResourcePermissionRepo;
    }

    public Collection<RoleResourcePermissionDTO> createRoleResourcePermissions(String roleId, String resourceId, Collection<ResourcePermissionTypeEnum> resourcePermissionTypes){
        if(CollectionUtils.isEmpty(resourcePermissionTypes))
            return Sets.newHashSet();
        Collection<RoleResourcePermissionDbo> resourcePermissionDbos = resourcePermissionTypes.stream()
                .map(resourcePermissionType -> new RoleResourcePermissionDbo(roleId, resourceId, resourcePermissionType))
                .collect(Collectors.toSet());

        return roleResourcePermissionRepo.saveAll(resourcePermissionDbos).stream()
                .map(ConverterUtils::getRoleResourcePermissionDto)
                .collect(Collectors.toSet());
    }

    public Collection<RoleResourcePermissionDTO> modifyRoleResourcePermissions(String roleId, String resourceId, Collection<ResourcePermissionTypeEnum> resourcePermissionTypes){
        if(CollectionUtils.isEmpty(resourcePermissionTypes)){
            roleResourcePermissionRepo.deleteValidRoleResourcePermissionByRoleIdAndResourceId(roleId, resourceId);
            return Sets.newHashSet();
        }

        roleResourcePermissionRepo.deleteValidRoleResourcePermissionByRoleIdAndResourceId(roleId, resourceId);
        return createRoleResourcePermissions(roleId, resourceId, resourcePermissionTypes);
    }

    public Collection<RoleResourcePermissionDTO> getRoleResourcePermissions(List<String> roleIds) {
        if (Objects.isNull(roleIds) || roleIds.size() == 0)
            return new HashSet<>();

        return roleResourcePermissionRepo
                .findValidRoleResourcePermissionByRoleId(roleIds.stream().filter(Objects::nonNull).collect(Collectors.toList()))
                .stream().map(ConverterUtils::getRoleResourcePermissionDto).collect(Collectors.toSet());
    }
}
