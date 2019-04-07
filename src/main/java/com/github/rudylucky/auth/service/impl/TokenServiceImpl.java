package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.common.UserStatus;
import com.github.rudylucky.auth.common.AuthConstants;
import com.github.rudylucky.auth.common.util.JsonUtils;
import com.github.rudylucky.auth.common.util.SystemConfig;
import com.github.rudylucky.auth.common.util.TimeUtils;
import com.github.rudylucky.auth.dto.UserDTO;
import com.github.rudylucky.auth.manager.UserManager;
import com.github.rudylucky.auth.service.TokenService;
import com.github.rudylucky.auth.common.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${settings.issuer}")
    private String issuer;

    @Value("${settings.secret}")
    private String secret;

    private UserManager userManager;

    @Autowired
    public TokenServiceImpl(UserManager userManager){
        this.userManager = userManager;
    }

    public Optional<String> verifyAndGetToken(UserDTO userDto, String password){
        boolean loginStatus = false;
        StringBuilder message = new StringBuilder().append("登录失败：");

        if (userDto.getLocked() || userDto.getTimesOfLoginFailure() > (Integer) SystemConfig.get(AuthConstants.MAX_LOGIN_FAILURE_TIMES)) {
                message.append("用户已经被锁定，请联系管理员!");
        } else if(userDto.getExpired() || TimeUtils.isSameOrBefore(userDto.getPasswordExpiredTimestamp())) {
            message.append("用户密码已过期，修改密码后方可登录!");
            userDto.setExpired(true);
        } else if(!CommonUtils.checkPassword(password, userDto.getPassword())){
            message.append("用户名或密码错误，请重新输入!");
            if (!AuthConstants.ADMIN.equals(userDto.getUsername())) {
                userDto.setTimesOfLoginFailure(userDto.getTimesOfLoginFailure() + 1);

                if (userDto.getTimesOfLoginFailure() > (Integer) SystemConfig.get(AuthConstants.MAX_LOGIN_FAILURE_TIMES))
                    userDto.setLocked(true);
            }
        } else {
            loginStatus = true;
        }

        if (loginStatus) {
            message.delete(0, message.length()).append("登录成功");
            userDto.setTimesOfLoginFailure(0);
        }

        UserDTO updatedUser = userManager.updateUserByUserDto(userDto);

        return loginStatus
                ? CommonUtils.generateToken(updatedUser, secret, issuer)
                    .map(token -> JsonUtils.toJson(new UserStatus(updatedUser, token, message.toString(), "0", userDto.getId())))
                : Optional.of(JsonUtils.toJson(new UserStatus(updatedUser, message.toString(), "0")));
    }

}
