package com.haru.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    long waitTime() default 10;
    long leaseTime() default 1;
    TimeUnit unit() default TimeUnit.SECONDS;
    String key() default "";
}
