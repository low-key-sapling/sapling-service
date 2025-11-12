package com.sapling.module.system.infrastructure.config;

import com.sapling.frame.mybatis.core.handler.CustomMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类，用于注册自定义的MyBatis相关组件
 */
@Configuration
public class MyBatisConfig {

    /**
     * 创建并注册自定义的元对象处理器
     * CustomMetaObjectHandler用于自动填充实体类中的特定字段，如创建时间、修改时间等
     *
     * @return CustomMetaObjectHandler的实例，用于处理MyBatis元对象
     */
    @Bean
    public CustomMetaObjectHandler metaObjectHandler() {
        return new CustomMetaObjectHandler();
    }
}
