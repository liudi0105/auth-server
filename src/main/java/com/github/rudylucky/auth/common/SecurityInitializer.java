package com.github.rudylucky.auth.common;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
	@Override
	protected boolean enableHttpSessionEventPublisher() {
		return true;
	}
}
