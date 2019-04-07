package com.github.rudylucky.auth.authaction;

import com.github.rudylucky.auth.dto.Department;
import com.github.rudylucky.auth.dto.DepartmentDTO;


public interface DepartmentAuthAction {

    DepartmentDTO createDepartment(String departmentName, String departmentType, String description, String parentId, Integer sort);

    Department getDepartmentById(String departmentId);

    Department updateDepartment(String departmentId, String departmentName, String departmentType, String description);

    Department moveDepartment(String departmentId, String parentId);

}
