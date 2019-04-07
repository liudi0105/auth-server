package com.github.rudylucky.auth.authaction;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.github.rudylucky.auth.manager.ManagerConfig;

@Configuration
@ComponentScan(basePackageClasses = AuthActionConfig.class)
@Import(ManagerConfig.class)
public class AuthActionConfig {
}
