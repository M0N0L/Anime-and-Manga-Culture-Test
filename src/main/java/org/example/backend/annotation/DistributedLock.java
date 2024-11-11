package org.example.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 定义锁名称
     */
    String key();

    /**
     * 定义持有锁的时间
     */
    long leaseTime() default 50000;

    /**
     * 等待时间
     */
    long waitTime() default 10000;

    /**
     * 等待时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
