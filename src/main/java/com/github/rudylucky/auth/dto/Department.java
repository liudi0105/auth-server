package com.github.rudylucky.auth.dto;

import com.github.rudylucky.auth.common.util.tree.TreeEntity;

public class Department extends TreeEntity<Department> {

    private String departmentName;
    private String departmentType;
    private String description;

    public Department(String id, Integer sort, String departmentName, String departmentType, String description){
        super(id, sort);
        this.departmentName = departmentName;
        this.departmentType = departmentType;
        this.description = description;
    }

    public Department(String id, Integer sort, Department parent
            , String departmentName, String departmentType, String description){
        this(id, sort, departmentName, departmentType, description);
        super.setParent(parent);
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(String departmentType) {
        this.departmentType = departmentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Department getParent(){
        return super.getParent();
    }
}
