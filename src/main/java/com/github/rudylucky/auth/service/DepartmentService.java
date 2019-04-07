package com.github.rudylucky.auth.service;

import com.github.rudylucky.auth.dto.CompanyInfo;
import com.github.rudylucky.auth.dto.Department;



public interface DepartmentService {

    Department authDepartmentCreate(String departmentName, String departmentType, String description, String parentId, Number sort);

    Department authDepartmentRemove(String departmentId);

    Department authDepartmentModify(String departmentId, String departmentName, String departmentType
            , String description);

    Department authDepartmentMove(String departmentId, String parentId);

    Department authDepartmentGet(String departmentId);

    Department authAllDepartmentGet();

    CompanyInfo authCompanyGet();
    CompanyInfo authCompanyCreate(String companyName, String companyType, String unifiedSocialCreditCode
            , String legalPerson, String contactEmail, String description);
}
