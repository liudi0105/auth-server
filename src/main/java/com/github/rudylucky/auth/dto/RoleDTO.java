package com.github.rudylucky.auth.dto;

public class RoleDTO {

    private String id;
    private String roleName;
    private String remark;
    private String alias;

    public RoleDTO(String id, String roleName, String remark, String alias) {
        this.id = id;
        this.roleName = roleName;
        this.remark = remark;
        this.alias = alias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
