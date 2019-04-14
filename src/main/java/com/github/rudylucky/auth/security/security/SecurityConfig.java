package com.github.rudylucky.auth.security.security;

import com.github.rudylucky.auth.security.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private AuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    public SecurityConfig(AuthenticationEntryPoint jwtAuthenticationEntryPoint
            , JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter){

        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().
                withUser("user").password("user").roles("USER").and().
                withUser("admin").password("admin").roles("USER", "ADMIN");
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .anonymous().disable()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .headers().cacheControl()
                .and()
                .frameOptions().disable();

        httpSecurity
                .csrf().disable()
                .exceptionHandling()
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, PathConstants.ARRAY_GET_PATHS).permitAll()
                    .antMatchers(HttpMethod.POST, PathConstants.ARRAY_POST_PATHS).permitAll()
                .anyRequest().authenticated();

        httpSecurity.sessionManagement().maximumSessions(1).expiredUrl("/");
    }
}
