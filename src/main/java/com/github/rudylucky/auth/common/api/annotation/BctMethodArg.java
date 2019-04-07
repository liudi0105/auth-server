package com.github.rudylucky.auth.common.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BctMethodArg {
    String name() default "";
    String description() default "";
    boolean required() default true;
}
