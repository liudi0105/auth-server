package com.github.rudylucky.auth.authaction;

import com.github.rudylucky.auth.manager.ManagerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

//@Configuration
@ComponentScan
@Import(ManagerConfig.class)
public class AuthActionConfig {
}
