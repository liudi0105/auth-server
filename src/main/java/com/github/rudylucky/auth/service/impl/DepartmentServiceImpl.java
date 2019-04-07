package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.business.DepartmentBusiness;
import com.github.rudylucky.auth.common.api.annotation.BctMethodArg;
import com.github.rudylucky.auth.common.api.annotation.BctMethodInfo;
import com.github.rudylucky.auth.common.util.CommonUtils;
import com.github.rudylucky.auth.dto.CompanyInfo;
import com.github.rudylucky.auth.dto.Department;
import com.github.rudylucky.auth.service.ApiParamConstants;
import com.github.rudylucky.auth.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentBusiness departmentBusiness;

    @Autowired
    public DepartmentServiceImpl(
            DepartmentBusiness departmentBusiness){
        this.departmentBusiness = departmentBusiness;
    }

    /*------------------------ department related api  --------------------------*/
    @Override
    @BctMethodInfo(
            retName = ApiParamConstants.DEPARTMENT
            , retDescription = ApiParamConstants.DEPARTMENT
            , description = "create department")
    @Transactional
    public Department authDepartmentCreate(
            @BctMethodArg(name = ApiParamConstants.DEPARTMENT_NAME) String departmentName
            , @BctMethodArg(name = ApiParamConstants.DEPARTMENT_TYPE) String departmentType
            , @BctMethodArg(required = false, name = ApiParamConstants.DESCRIPTION) String description
            , @BctMethodArg(name = ApiParamConstants.PARENT_ID) String parentId
            , @BctMethodArg(name = ApiParamConstants.SORT) Number sort) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.DEPARTMENT_NAME, departmentName);
            put(ApiParamConstants.DEPARTMENT_TYPE, departmentType);
            put(ApiParamConstants.PARENT_ID, parentId);
        }});

        departmentBusiness.createDepartment(departmentName, departmentType, description, parentId, sort.intValue());
        return departmentBusiness.getDepartment();
    }

    @Override
    @BctMethodInfo(
            retName = ApiParamConstants.DEPARTMENT
            , retDescription = ApiParamConstants.DEPARTMENT
            , description = "remove department")
    @Transactional
    public Department authDepartmentRemove(@BctMethodArg(name = ApiParamConstants.DEPARTMENT_ID) String departmentId) {
        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.DEPARTMENT_ID, departmentId);
        }});

        departmentBusiness.deleteDepartment(departmentId);
        return departmentBusiness.getDepartment();
    }

    @Override
    @BctMethodInfo(
            retName = ApiParamConstants.DEPARTMENT
            , retDescription = ApiParamConstants.DEPARTMENT
            , description = "update a department")
    @Transactional
    public Department authDepartmentModify(
            @BctMethodArg(name = ApiParamConstants.DEPARTMENT_ID) String departmentId,
            @BctMethodArg(name = ApiParamConstants.DEPARTMENT_NAME) String departmentName,
            @BctMethodArg(name = ApiParamConstants.DEPARTMENT_TYPE) String departmentType,
            @BctMethodArg(required = false, name = ApiParamConstants.DESCRIPTION) String description) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.DEPARTMENT_ID, departmentId);
            put(ApiParamConstants.DEPARTMENT_NAME, departmentName);
            put(ApiParamConstants.DEPARTMENT_TYPE, departmentType);
        }});

        departmentBusiness.updateDepartment(departmentId, departmentName, departmentType, description);
        return departmentBusiness.getDepartment();
    }

    @Override
    @BctMethodInfo(
            retName = ApiParamConstants.DEPARTMENT
            , retDescription = ApiParamConstants.DEPARTMENT
            , description = "remove a department")
    @Transactional
    public Department authDepartmentMove(
            @BctMethodArg(name = ApiParamConstants.DEPARTMENT_ID) String departmentId,
            @BctMethodArg(name = ApiParamConstants.PARENT_ID) String parentId) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.DEPARTMENT_ID, departmentId);
            put(ApiParamConstants.PARENT_ID, parentId);
        }});

        return departmentBusiness.moveDepartment(departmentId, parentId);
    }

    @Override
    @BctMethodInfo(retName = ApiParamConstants.DEPARTMENT, retDescription = ApiParamConstants.DEPARTMENT,
            description = "get department tree by department id")
    @Transactional
    public Department authDepartmentGet(@BctMethodArg(name = ApiParamConstants.DEPARTMENT_ID) String departmentId) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.DEPARTMENT_ID, departmentId);
        }});

        return departmentBusiness.getDepartment(departmentId);
    }

    @Override
    @BctMethodInfo(retName = ApiParamConstants.DEPARTMENT, retDescription = ApiParamConstants.DEPARTMENT
            , description = "get company's department")
    @Transactional
    public Department authAllDepartmentGet(){
        return departmentBusiness.getDepartment();
    }

    /*------------------------ department related api  --------------------------*/

    /*------------------------ company related api  --------------------------*/
    @Override
    @BctMethodInfo(
            retDescription = ApiParamConstants.COMPANY_INFO
            , retName = ApiParamConstants.COMPANY_INFO
            , description = "create company info")
    @Transactional
    public CompanyInfo authCompanyCreate(String companyName, String companyType, String unifiedSocialCreditCode
            , String legalPerson, String contactEmail, String description) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.COMPANY_NAME, companyName);
            put(ApiParamConstants.COMPANY_TYPE, companyType);
            put(ApiParamConstants.UNIFIED_SOCIAL_CREDIT_CODE, unifiedSocialCreditCode);
            put(ApiParamConstants.LEGAL_PERSON, legalPerson);
            put(ApiParamConstants.CONTACT_EMAIL, contactEmail);
        }});

        return departmentBusiness.createCompanyInfo(companyName, companyType, unifiedSocialCreditCode, legalPerson, contactEmail, description);
    }

    @BctMethodInfo(
            retDescription = ApiParamConstants.COMPANY_INFO
            , retName = ApiParamConstants.COMPANY_INFO
            , description = "update company info")
    @Transactional
    public CompanyInfo authCompanyUpdate(String companyName, String companyType, String unifiedSocialCreditCode
            , String legalPerson, String contactEmail, String description) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.COMPANY_NAME, companyName);
            put(ApiParamConstants.COMPANY_TYPE, companyType);
            put(ApiParamConstants.UNIFIED_SOCIAL_CREDIT_CODE, unifiedSocialCreditCode);
            put(ApiParamConstants.LEGAL_PERSON, legalPerson);
            put(ApiParamConstants.CONTACT_EMAIL, contactEmail);
        }});

        return departmentBusiness.updateCompanyInfo(companyName, companyType, unifiedSocialCreditCode, legalPerson, contactEmail, description);
    }

    @Override
    @BctMethodInfo(
            retDescription = ApiParamConstants.COMPANY_INFO
            , retName = ApiParamConstants.COMPANY_INFO
            , description = "get company info")
    @Transactional
    public CompanyInfo authCompanyGet(){
        return departmentBusiness.getCompanyInfo();
    }
    /*------------------------ company related api  --------------------------*/

}
