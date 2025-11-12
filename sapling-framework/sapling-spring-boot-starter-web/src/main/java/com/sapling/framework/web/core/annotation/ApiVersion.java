package com.sapling.framework.web.core.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * 接口版本标识注解
 * @author artisan
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface ApiVersion {
    /**
     * 指定API的版本号。
     * 此方法返回一个整型数组，数组中的每个元素代表一个API版本号。
     *
     * @return 代表API版本号的整数数组。
     */
    int  value();
}
