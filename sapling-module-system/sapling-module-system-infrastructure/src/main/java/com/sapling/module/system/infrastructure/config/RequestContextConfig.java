package com.sapling.module.system.infrastructure.config;

import com.sapling.framework.core.context.decorator.RequestContextDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 请求上下文配置类
 */
@Configuration
public class RequestContextConfig {

    /**
     * 注册请求上下文装饰器
     */
    @Bean
    public RequestContextDecorator requestContextDecorator() {
        return new RequestContextDecorator();
    }
} 