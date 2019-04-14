package com.github.rudylucky.auth.service.impl;

import com.github.rudylucky.auth.cache.RedisConfig;
import com.github.rudylucky.auth.manager.ManagerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = ServiceConfig.class)
@Import({ManagerConfig.class, RedisConfig.class})
public class ServiceConfig {

}
