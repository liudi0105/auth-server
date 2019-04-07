package com.github.rudylucky.auth.security.security;

import com.github.rudylucky.auth.common.UserInfo;
import com.github.rudylucky.auth.common.exception.JwtAuthenticationException;
import com.github.rudylucky.auth.common.util.TokenConstants;
import com.github.rudylucky.auth.common.util.TokenUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Value("${secret:dkJ34Bdadf098adf}")
    private String secret = "dkJ34Bdadf098adf";

    private UserInfo userInfo;

    @Autowired
    public TokenAuthenticationProvider(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;
        if(jwtAuthentication.invalidToken())
            throw new JwtAuthenticationException();
        if(!jwtAuthentication.newLoginUser()){
            Claims claims = TokenUtils.getClaimsFromToken(secret, jwtAuthentication.getToken());
            userInfo.setUserName((String) claims.get(TokenConstants.USERNAME));
            userInfo.setToken(jwtAuthentication.getToken());
        }
        return jwtAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Objects.equals(authentication, JwtAuthentication.class);
    }
}
