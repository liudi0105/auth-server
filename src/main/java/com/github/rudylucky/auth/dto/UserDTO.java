package com.github.rudylucky.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.rudylucky.auth.common.util.CustomDateSerializer;
import com.github.rudylucky.auth.enums.UserTypeEnum;

import java.sql.Timestamp;
import java.util.Collection;

public class UserDTO {

    private String id;
    private String username;
    private String nickName;
    private String contactEmail;
    private UserTypeEnum userType;
    private Boolean locked;
    private Boolean expired;
    private Integer timesOfLoginFailure;
    private String departmentId;

    @JsonIgnore
    private String password;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Timestamp passwordExpiredTimestamp;

    private Collection<String> roleName;

    public UserDTO(String id, String username, String nickName, String contactEmail, String password
            , UserTypeEnum userType, Boolean locked, Boolean expired, Integer timesOfLoginFailure
            , Timestamp passwordExpiredTimestamp, String departmentId, Collection<String> roleName) {
        this.id = id;
        this.username = username;
        this.nickName = nickName;
        this.contactEmail = contactEmail;
        this.userType = userType;
        this.password = password;
        this.locked = locked;
        this.expired = expired;
        this.timesOfLoginFailure = timesOfLoginFailure;
        this.passwordExpiredTimestamp = passwordExpiredTimestamp;
        this.departmentId = departmentId;
        this.roleName = roleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public UserTypeEnum getUserType() {
        return userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(UserTypeEnum userType) {
        this.userType = userType;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Integer getTimesOfLoginFailure() {
        return timesOfLoginFailure;
    }

    public void setTimesOfLoginFailure(Integer timesOfLoginFailure) {
        this.timesOfLoginFailure = timesOfLoginFailure;
    }

    public Timestamp getPasswordExpiredTimestamp() {
        return passwordExpiredTimestamp;
    }

    public void setPasswordExpiredTimestamp(Timestamp passwordExpiredTimestamp) {
        this.passwordExpiredTimestamp = passwordExpiredTimestamp;
    }

    public Collection<String> getRoleName() {
        return roleName;
    }

    public void setRoleName(Collection<String> roleName) {
        this.roleName = roleName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isLoginPermitted(){
        return !getExpired() && !getLocked();
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
