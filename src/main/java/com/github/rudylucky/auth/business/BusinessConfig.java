package com.github.rudylucky.auth.business;

import com.github.rudylucky.auth.authaction.AuthActionConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

//@Configuration
@ComponentScan(basePackageClasses = BusinessConfig.class)
@Import({AuthActionConfig.class})
public class BusinessConfig {
}
