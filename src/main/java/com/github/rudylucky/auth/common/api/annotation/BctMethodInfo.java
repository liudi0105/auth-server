package com.github.rudylucky.auth.common.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BctMethodInfo {
    String description() default "";
    String retName() default "result";
    String retDescription() default "";
}
