package com.github.rudylucky.auth.business;

import com.github.rudylucky.auth.authaction.AuthActionConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.github.rudylucky.auth.manager.ManagerConfig;

@Configuration
@ComponentScan(basePackageClasses = BusinessConfig.class)
@Import({AuthActionConfig.class, ManagerConfig.class})
public class BusinessConfig {
}
