package com.github.rudylucky.auth.service;

import com.github.rudylucky.auth.business.BusinessConfig;
import com.github.rudylucky.auth.cache.RedisConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

//@Configuration
@ComponentScan
@Import({BusinessConfig.class, RedisConfig.class})
public class ServiceConfig {

}
