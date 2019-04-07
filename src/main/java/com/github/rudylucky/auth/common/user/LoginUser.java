package com.github.rudylucky.auth.common.user;

import com.github.rudylucky.auth.common.AuthConstants;
import com.github.rudylucky.auth.common.exception.AuthServiceException;
import com.github.rudylucky.auth.common.exception.ReturnMessageAndTemplateDef;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class LoginUser {

    private String username;
    private String password;
    private String captcha;

    public static LoginUser of(Map<String, Object> body){
        String username = (String) body.get(AuthConstants.USERNAME);
        if(StringUtils.isBlank(username))
            username = (String) body.get(AuthConstants.USER_NAME);
        String password = (String) body.get(AuthConstants.PASSWORD);
        String captcha = (String) body.get(AuthConstants.CAPTCHA);
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password))
            throw new AuthServiceException( ReturnMessageAndTemplateDef.Errors.USERNAME_OR_PASSWORD_NOT_VALID);
        return new LoginUser(username, password, captcha);
    }

    public LoginUser(String username, String password, String captcha){
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

}
