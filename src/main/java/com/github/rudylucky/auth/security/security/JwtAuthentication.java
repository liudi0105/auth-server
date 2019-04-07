package com.github.rudylucky.auth.security.security;

import com.github.rudylucky.auth.common.util.TokenConstants;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Objects;

public class JwtAuthentication extends UsernamePasswordAuthenticationToken {

    private Boolean invalidToken;
    private Boolean newLoginUser;

    public JwtAuthentication(String token, WebAuthenticationDetails details) {
        super(token, null);
        setAuthenticated(false);
        super.setDetails(details);
        this.invalidToken = Objects.equals(TokenConstants.INVALID_TOKEN, token);
        this.newLoginUser = Objects.equals(TokenConstants.NEW_LOGIN_USER, token);
    }

    public Boolean invalidToken(){
        return invalidToken;
    }

    public Boolean newLoginUser() {return newLoginUser;}

    public String getToken(){
        return (String) super.getPrincipal();
    }

    public String getIpAddress(){
        return ((WebAuthenticationDetails) super.getDetails()).getRemoteAddress();
    }
}
