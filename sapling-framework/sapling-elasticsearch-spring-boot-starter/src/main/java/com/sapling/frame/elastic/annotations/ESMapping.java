package com.sapling.frame.elastic.annotations;

import com.sapling.frame.elastic.enums.ESMappingType;

import java.lang.annotation.*;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 * @description 为字段指定映射类型
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ESMapping {

    //映射类型
    ESMappingType value();

    //加权
    int boost() default 1;

    //分词标识analyzed、not_analyzed
    String index() default "analyzed";

    //分词器ik_max_word、standard
    String analyzer() default "standard";

    //String作为分组聚合字段的时候需要设置为true
    boolean fielddata() default false;
}
