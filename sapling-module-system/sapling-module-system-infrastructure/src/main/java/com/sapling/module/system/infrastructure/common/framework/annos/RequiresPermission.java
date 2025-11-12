package com.sapling.module.system.infrastructure.common.framework.annos;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    // 定义菜单权限标识
    String[] value() default {};
}
