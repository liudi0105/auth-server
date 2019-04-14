package com.github.rudylucky.auth.security;

import com.github.rudylucky.auth.common.UserInfo;
import org.springframework.context.annotation.*;
import org.springframework.web.context.WebApplicationContext;

@Configuration
//@ComponentScan
public class AuthClientConfig {
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserInfo user() {
        return new UserInfo();
    }
}
