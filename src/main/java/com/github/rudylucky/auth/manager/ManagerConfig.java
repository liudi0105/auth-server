package com.github.rudylucky.auth.manager;

import com.github.rudylucky.auth.dao.DaoConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = ManagerConfig.class)
@Import(DaoConfig.class)
public class ManagerConfig {
}
