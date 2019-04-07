package com.github.rudylucky.auth.manager;

import com.github.rudylucky.auth.common.UserInfo;
import com.github.rudylucky.auth.common.exception.ManagerException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import com.github.rudylucky.auth.common.util.CommonUtils;
import com.github.rudylucky.auth.dao.DepartmentRepo;
import com.github.rudylucky.auth.dao.RoleRepo;
import com.github.rudylucky.auth.dao.UserRepo;
import com.github.rudylucky.auth.dao.entity.UserDbo;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.dto.UserStatusDTO;
import com.github.rudylucky.auth.enums.UserTypeEnum;
import com.github.rudylucky.auth.common.util.ConverterUtils;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserManager {

    private UserRepo userRepo;
    private RoleRepo roleRepo;
    private UserInfo userInfo;
    private DepartmentRepo departmentRepo;

    @Autowired
    public UserManager(UserRepo userRepo, RoleRepo roleRepo, DepartmentRepo departmentRepo, UserInfo userInfo){
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.departmentRepo = departmentRepo;
        this.userInfo = userInfo;
    }

    public UserDTO getCurrentUser(){
        return this.getUserByUserName(userInfo.getUserName());
    }

    public UserDTO getUserByUserId(String userId){
        return userRepo.findValidUserById(userId)
                .map(ConverterUtils::getUserDto)
                .orElseThrow(() -> new ManagerException(ReturnMessageAndTemplateDef.Errors.NO_SUCH_USER, userId));
    }

    public void throwExceptionIfUserExists(String username){
        if(userRepo.findValidUserByUsername(username).isPresent())
            throw new ManagerException(ReturnMessageAndTemplateDef.Errors.USER_EXISTS, username);
    }

    public UserDTO createUser(String username, String nickname, String contactEmail, String password
            , UserTypeEnum userType, String departmentId){
        return departmentRepo.findValidDepartmentById(departmentId)
                .map(departmentDbo -> {
                    UserDbo user = new UserDbo();
                    user.setUsername(username);
                    user.setNickName(nickname);
                    user.setContactEmail(contactEmail);
                    user.setPassword(CommonUtils.hashPassword(password));
                    user.setLocked(false);
                    user.setExpired(false);
                    user.setUserType(userType);
                    user.setTimesOfLoginFailure(0);
                    user.setDepartmentDbo(departmentDbo);
                    user.setPasswordExpiredTimestamp(CommonUtils.getPasswordExpirationTimestamp(userType));
                    return ConverterUtils.getUserDto(userRepo.save(user));
                })
                .orElseThrow(() -> new ManagerException(ReturnMessageAndTemplateDef.Errors.MISSING_DEPARTMENT, departmentId));
    }

    public UserStatusDTO updateUserLocked(String username, Boolean locked){
        return ConverterUtils.getUserStatusDTO(findByUserParamAndChangeUser(userRepo::findValidUserByUsername, user -> {
            user.setLocked(locked);
            if (!locked) {
                user.setTimesOfLoginFailure(0);
            }
            return userRepo.save(user);
        }).apply(username));
    }

    public UserStatusDTO updateUserExpired(String username, Boolean expired){
        return ConverterUtils.getUserStatusDTO(findByUserParamAndChangeUser(userRepo::findValidUserByUsername, user -> {
            user.setExpired(expired);
            if(!expired) user.setPasswordExpiredTimestamp(CommonUtils.getPasswordExpirationTimestamp(user.getUserType()));
            return userRepo.save(user);
        }).apply(username));
    }

    @Transactional
    public UserDTO revokeUser(String userId){
        return findByUserParamAndChangeUser(userRepo::findValidUserById, user -> {
            user.setRevoked(true);
            roleRepo.findRoleByRoleName(CommonUtils.getDefaultRoleName(user.getUsername()))
                    .map(role -> {
                        role.setRevoked(true);
                        return roleRepo.save(role);
                    });
            return userRepo.save(user);
        }).apply(userId);
    }

    @Transactional
    public UserDTO updateUserAttributes(String userId, String username, String nickName, UserTypeEnum userType, String contactEmail, String departmentId){
        return findByUserParamAndChangeUser(userRepo::findValidUserById, user -> {
            user.setUsername(username);
            user.setNickName(nickName);
            user.setUserType(userType);
            user.setContactEmail(contactEmail);
            departmentRepo.findValidDepartmentById(departmentId)
                    .map(departmentDbo -> {
                        user.setDepartmentDbo(departmentDbo);
                        return departmentDbo;
                    }).orElseThrow(() -> new ManagerException(ReturnMessageAndTemplateDef.Errors.MISSING_DEPARTMENT, departmentId));
            return userRepo.save(user);
        }).apply(userId);
    }

    public UserDTO updateUserRoles(String userId, List<String> roleIdList){
        return findByUserParamAndChangeUser(userRepo::findValidUserById, user -> {
            if (roleIdList.size() == 0) {
                user.getRoleDbos().clear();
            } else {
                user.setRoleDbos(roleRepo.findValidRolesByRoleIds(roleIdList));
            }
            return userRepo.save(user);
        }).apply(userId);
    }

    public UserDTO updatePassword(String userId, String password){
        return findByUserParamAndChangeUser(userRepo::findValidUserById, user -> {
            user.setPassword(CommonUtils.hashPassword(password));
            user.setExpired(false);
            user.setPasswordExpiredTimestamp(CommonUtils.getPasswordExpirationTimestamp(user.getUserType()));
            return userRepo.save(user);
        }).apply(userId);
    }

    public UserDTO updateOwnPassword(String username, String oldPassword, String newPassword){
        return findByUserParamAndChangeUser(userRepo::findValidUserByUsername, user -> {
            if(CommonUtils.checkPassword(oldPassword, user.getPassword())){
                user.setPassword(CommonUtils.hashPassword(newPassword));
                user.setPasswordExpiredTimestamp(CommonUtils.getPasswordExpirationTimestamp(user.getUserType()));
                user.setExpired(false);
            }
            else throw new ManagerException(ReturnMessageAndTemplateDef.Errors.INCORRECT_PASSWORD);
            return userRepo.save(user);
        }).apply(username);
    }

    public Collection<UserDTO> findAllValidUser(){
        return userRepo.findAllValidUser().stream().map(ConverterUtils::getUserDto).collect(Collectors.toSet());
    }

    public UserDTO findUserByUserId(String userId){
        return findByUserParamAndChangeUser(userRepo::findValidUserById, Function.identity())
                .apply(userId);
    }

    public UserDTO getUserByUserName(String username){
        return findByUserParamAndChangeUser(userRepo::findValidUserByUsername, Function.identity())
                .apply(username);
    }

    public UserDTO updateUserByUserDto(UserDTO userDto){
        return findByUserParamAndChangeUser(userRepo::findValidUserByUsername, user -> {
            user.setNickName(userDto.getNickName());
            user.setExpired(userDto.getExpired());
            user.setTimesOfLoginFailure(userDto.getTimesOfLoginFailure());
            user.setLocked(userDto.getLocked());
            user.setUserType(userDto.getUserType());
            user.setPasswordExpiredTimestamp(userDto.getPasswordExpiredTimestamp());
            return userRepo.save(user);
        }).apply(userDto.getUsername());
    }

    public void resetLockedAndExpiredOfAllUser(){
        userRepo.resetLockedAndExpiredOfAllValidUser(CommonUtils.getPasswordExpirationTimestamp());
    }

    private Function<String, UserDTO> findByUserParamAndChangeUser(Function<String, Optional<UserDbo>> findFunc, Function<UserDbo, UserDbo> operationOnUser){
        return (param) -> findFunc.apply(param)
                .map(operationOnUser)
                .map(ConverterUtils::getUserDto)
                .orElseThrow(() -> new ManagerException(ReturnMessageAndTemplateDef.Errors.NO_SUCH_USER, param));
    }

    public Boolean isOtherUserExists(String userId, String username, String departmentId) {
        return userRepo.countValidUserByOtherIdAndUsernameAndDepartmentId(userId, username, departmentId) > 0;
    }

    public Boolean isUserExists(String username){
        return userRepo.countValidUserByUsername(username) > 0;
    }

    public Collection<UserDTO> listUserByDepartmentId(Collection<String> departmentId){
        if(CollectionUtils.isEmpty(departmentId))
            return Sets.newHashSet();
        return userRepo.findValidUserByDepartmentId(departmentId)
                .stream()
                .map(ConverterUtils::getUserDto)
                .collect(Collectors.toSet());
    }

}
