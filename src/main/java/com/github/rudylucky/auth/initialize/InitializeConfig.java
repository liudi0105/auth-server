package com.github.rudylucky.auth.initialize;

import com.github.rudylucky.auth.service.ServiceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = InitializeConfig.class)
@Import(ServiceConfig.class)
public class InitializeConfig {
}
