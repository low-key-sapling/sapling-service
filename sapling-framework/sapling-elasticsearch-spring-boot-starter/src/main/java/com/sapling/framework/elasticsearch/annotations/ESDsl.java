package com.sapling.framework.elasticsearch.annotations;

import java.lang.annotation.*;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ESDsl {
    /**
     * xml的位置
     */
    String value();

    /**
     * 索引名称
     */
    String indexName();

    /**
     * elasticsearch7.x版本已经删除该属性
     */
    String indexType() default "";
}