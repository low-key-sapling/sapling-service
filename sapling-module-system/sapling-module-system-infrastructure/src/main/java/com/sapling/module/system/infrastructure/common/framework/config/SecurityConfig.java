package com.sapling.module.system.infrastructure.common.framework.config;

import com.sapling.module.system.infrastructure.common.framework.filters.SecurityFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<SecurityFilter> securityFilterRegistration(SecurityFilter securityFilter) {
        FilterRegistrationBean<SecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(securityFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 确保在其他过滤器之前执行
        registration.setName("securityFilter");
        return registration;
    }
} 