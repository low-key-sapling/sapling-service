package com.sapling.framework.web.config;

import com.sapling.framework.web.core.version.ApiVersionProperties;
import com.sapling.framework.web.core.version.ApiVersionWebMvcRegistrations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ApiVersionAutoConfiguration类用于自动配置基于Spring MVC的API版本控制
 * 该类通过@EnableConfigurationProperties注解激活ApiVersionProperties配置类
 * 并且通过@Bean注解的方法创建和管理ApiVersionWebMvcRegistrations的单例对象
 * @author Artisan
 */
@ConditionalOnWebApplication
@Configuration
@EnableConfigurationProperties(ApiVersionProperties.class)
public class ApiVersionAutoConfiguration {

    /**
     * 通过@Bean注解声明此方法将返回一个单例对象，由Spring容器管理
     * 该方法的目的是根据ApiVersionProperties配置生成ApiVersionWebMvcRegistrations实例
     * 这对于自动配置基于Spring MVC的API版本控制至关重要
     *
     * @param apiVersionProperties 一个包含API版本控制相关配置的实体类
     *                             该参数用于初始化ApiVersionWebMvcRegistrations对象
     * @return 返回一个ApiVersionWebMvcRegistrations对象，用于注册和管理API版本控制相关的设置
     */
    @Bean
    public ApiVersionWebMvcRegistrations apiVersionWebMvcRegistrations(ApiVersionProperties apiVersionProperties) {
        return new ApiVersionWebMvcRegistrations(apiVersionProperties);
    }
}

