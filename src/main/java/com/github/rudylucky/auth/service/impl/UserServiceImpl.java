package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.authaction.UserAuthAction;
import com.github.rudylucky.auth.common.api.annotation.BctMethodArg;
import com.github.rudylucky.auth.common.api.annotation.BctMethodInfo;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.dto.UserStatusDTO;
import com.github.rudylucky.auth.enums.UserTypeEnum;
import com.github.rudylucky.auth.service.ApiParamConstants;
import com.github.rudylucky.auth.service.UserService;
import com.github.rudylucky.auth.common.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserAuthAction userAuthAction;

    @Autowired
    public UserServiceImpl(
            UserAuthAction userAuthAction){
        this.userAuthAction = userAuthAction;
    }

    @BctMethodInfo(
            description = "Create LoginUser",
            retName = "user",
            retDescription = "user")
    @Transactional
    public UserDTO authUserCreate(
            @BctMethodArg(name = ApiParamConstants.USERNAME, description = "user name") String username,
            @BctMethodArg(name = ApiParamConstants.PASSWORD, description = "password") String password,
            @BctMethodArg(name = ApiParamConstants.USER_TYPE, description = "userType") String userType,
            @BctMethodArg(name = ApiParamConstants.NICK_NAME, description = "nick name", required = false) String nickName,
            @BctMethodArg(name = ApiParamConstants.CONTACT_EMAIL, description = "contact email", required = false) String contactEmail,
            @BctMethodArg(name = ApiParamConstants.DEPARTMENT_ID, description = "departmentId") String departmentId,
            @BctMethodArg(name = ApiParamConstants.ROLE_IDS, description = "roleIds") List<String> roleIds
    ) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USERNAME, username);
            put(ApiParamConstants.PASSWORD, password);
            put(ApiParamConstants.USER_TYPE, userType);
            put(ApiParamConstants.DEPARTMENT_ID, departmentId);
        }});

        return userAuthAction.createUser(username, nickName, contactEmail, password, UserTypeEnum.of(userType), departmentId, roleIds);
    }

    @BctMethodInfo(
            description = "Get LoginUser By Name",
            retName = "user",
            retDescription = "user"
    )
    public UserDTO authUserByNameGet(@BctMethodArg(name = ApiParamConstants.USERNAME, description = "user name") String username) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USERNAME, username);
        }});

        return userAuthAction.getUserByUsername(username);
    }

    @BctMethodInfo(
            description = "Get All LoginUser",
            retName = "users",
            retDescription = "user list"
    )
    public Collection<UserDTO> authUserList() {
        return userAuthAction.listUsers();
    }

    @BctMethodInfo(
            description = "Get All LoginUser",
            retName = "users",
            retDescription = "user list"
    )
    @Transactional
    public Collection<String> authUserListByBookCanRead() {
        return userAuthAction.listUsersByBookCanRead();
    }

    @Override
    public Boolean authIsUserValid(String username) {
        return userAuthAction.isUserValid(username);
    }

    @BctMethodInfo(description = "lock user", retName = "success or failure", retDescription = "success or failure")
    @Transactional
    public UserStatusDTO authUserLock(@BctMethodArg(name = ApiParamConstants.USERNAME, description = "username") String username) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USERNAME, username);
        }});

        return userAuthAction.updateUserLocked(username, true);
    }

    @BctMethodInfo(description = "lock user", retName = "success or failure", retDescription = "success or failure")
    @Transactional
    public UserStatusDTO authUserUnlock(@BctMethodArg(name = ApiParamConstants.USERNAME, description = "user name") String username) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USERNAME, username);
        }});

        return userAuthAction.updateUserLocked(username, false);
    }

    @BctMethodInfo(description = "expire user", retName = "success or failure", retDescription = "success or failure")
    @Transactional
    public UserStatusDTO authUserExpire(@BctMethodArg(name = ApiParamConstants.USERNAME, description = "user name") String username) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USERNAME, username);
        }});

        return userAuthAction.updateUserExpired(username, true);
    }

    @BctMethodInfo(description = "make user no more expired", retName = "success or failure", retDescription = "success or failure")
    @Transactional
    public UserStatusDTO authUserUnexpire(@BctMethodArg(name = ApiParamConstants.USERNAME, description = "user name") String username) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USERNAME, username);
        }});

        return userAuthAction.updateUserExpired(username, false);
    }

    @BctMethodInfo(description = "get current user name", retName = "userDTO", retDescription = "user info")
    public UserDTO authCurrentUserGet() {
        return userAuthAction.getCurrentUser();
    }

    @BctMethodInfo(
            description = "Change LoginUser's Password",
            retName = "user",
            retDescription = "user"
    )
    @Transactional
    public UserDTO authUserPasswordChange(
            @BctMethodArg(name = ApiParamConstants.USER_ID, description = "user id") String userId,
            @BctMethodArg(name = ApiParamConstants.PASSWORD, description = "password") String password) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USER_ID, userId);
            put(ApiParamConstants.PASSWORD, password);
        }});

        return userAuthAction.updatePassword(userId, password);
    }

    @BctMethodInfo(
            description = "Update LoginUser",
            retName = "user",
            retDescription = "user")
    @Transactional
    public UserDTO authUserUpdate(
            @BctMethodArg(name = ApiParamConstants.USER_ID, description = "user id") String userId,
            @BctMethodArg(name = ApiParamConstants.USERNAME, description = "username") String username,
            @BctMethodArg(required = false, name = ApiParamConstants.NICK_NAME, description = "nickName") String nickName,
            @BctMethodArg(name = ApiParamConstants.USER_TYPE, description = "userType") String userType,
            @BctMethodArg(required = false, name = ApiParamConstants.CONTACT_EMAIL, description = "contactEmail") String contactEmail,
            @BctMethodArg(name = ApiParamConstants.DEPARTMENT_ID, description = "departmentId") String departmentId) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USER_ID, userId);
            put(ApiParamConstants.USERNAME, username);
            put(ApiParamConstants.USER_TYPE, userType);
            put(ApiParamConstants.DEPARTMENT_ID, departmentId);
        }});

        return userAuthAction.updateUserAttributes(userId, username.trim(), nickName, UserTypeEnum.of(userType)
                , contactEmail, departmentId);
    }

    @BctMethodInfo(
            description = "Delete LoginUser",
            retName = "user",
            retDescription = "user")
    @Transactional
    public Boolean authUserRevoke(@BctMethodArg(name = ApiParamConstants.USER_ID, description = "user id") String userId) {

        CommonUtils.checkBlankParam(new HashMap<String, String>() {{
            put(ApiParamConstants.USER_ID, userId);
        }});

        userAuthAction.revokeUser(userId);
        return true;
    }

}
