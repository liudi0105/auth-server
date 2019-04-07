package com.github.rudylucky.auth.controller;

import com.github.rudylucky.auth.captcha.CaptchaConfig;
import com.github.rudylucky.auth.service.impl.ServiceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = ControllerConfig.class)
@Import({CaptchaConfig.class, ServiceConfig.class})
public class ControllerConfig {
}
