package com.github.rudylucky.auth.service.impl;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import com.github.rudylucky.auth.cache.RedisConfig;
import com.github.rudylucky.auth.manager.ManagerConfig;

@Configuration
@ComponentScan(basePackageClasses = ServiceConfig.class)
@Import({ManagerConfig.class, RedisConfig.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ServiceConfig {

}
