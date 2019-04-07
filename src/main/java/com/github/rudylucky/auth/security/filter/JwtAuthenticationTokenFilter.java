package com.github.rudylucky.auth.security.filter;

import com.github.rudylucky.auth.common.UserInfo;
import com.github.rudylucky.auth.security.security.PathConstants;
import com.github.rudylucky.auth.common.util.TokenConstants;
import com.github.rudylucky.auth.common.util.TokenUtils;
import com.google.common.collect.Lists;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Value("${secret:dkJ34Bdadf098adf}")
    private String secret = "dkJ34Bdadf098adf";

    private UserInfo userInfo;

    @Autowired
    public JwtAuthenticationTokenFilter(UserInfo userInfo){
        this.userInfo = userInfo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication;
        WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);

        boolean doNotCheck = Lists.newArrayList(
                PathConstants.USERS_CAPTCHA,
                PathConstants.USERS_LOGIN
        ).contains(request.getServletPath());

        doNotCheck = doNotCheck || Lists.newArrayList(
                PathConstants.WS_END_POINT,
                PathConstants.FILE_DOWNLOAD
        ).stream().anyMatch(v -> StringUtils.startsWith(request.getServletPath(), v));

        UserDetails userDetails;
        if(doNotCheck){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            userDetails = new UserDetailsImpl("newLoginUser", "", Lists.newArrayList(), true, calendar.getTime());
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Lists.newArrayList());
            authentication.setDetails(details);
        }
        else {
            Optional<String> tokenOpt = TokenUtils.extractTokenString(request);
            if(!tokenOpt.isPresent()){
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = TokenUtils.getClaimsFromToken(secret, tokenOpt.get());
            String username = (String) claims.get(TokenConstants.USERNAME);

            userInfo.setUserName(username);
            userInfo.setToken(tokenOpt.get());

            userDetails = new UserDetailsImpl(userInfo.getUserName(), "", Lists.newArrayList(), true, claims.getExpiration());
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Lists.newArrayList());
            authentication.setDetails(details);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
