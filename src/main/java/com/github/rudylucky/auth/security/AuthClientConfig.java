package com.github.rudylucky.auth.security;

import com.github.rudylucky.auth.common.UserInfo;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = AuthClientConfig.class)
public class AuthClientConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserInfo user() {
        return new UserInfo();
    }
}
