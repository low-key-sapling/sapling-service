package com.sapling.module.system.infrastructure.common.framework.configuration;

import com.sapling.module.system.infrastructure.common.framework.interceptors.SecurityInterceptor;
import com.sapling.module.system.infrastructure.common.framework.interceptors.SessionInterceptor;
import com.sapling.module.system.infrastructure.common.framework.interceptors.TokenAuthInterceptor;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.framework.filters.SecurityFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author mbws
 */
@Slf4j
@Configuration
public class EpcWebMvcConfigurer implements WebMvcConfigurer {

    @Resource
    private SecurityInterceptor securityInterceptor;

    @Resource
    private TokenAuthInterceptor tokenAuthInterceptor;

    @Resource
    private SessionInterceptor sessionInterceptor;

    @Value("${endpoint.security.tokenInterceptUrl:}")
    private String tokenInterceptUrlConfig;

    @Value("${endpoint.security.sessionInterceptUrl:}")
    private String sessionInterceptUrlConfig;


    @Bean
    public FilterRegistrationBean<SecurityFilter> securityFilterRegistration(SecurityFilter securityFilter) {
        FilterRegistrationBean<SecurityFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(securityFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 确保在其他过滤器之前执行
        registration.setName("securityFilter");
        return registration;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册安全拦截器
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/**");

        // 解析Token拦截URL配置
        List<String> tokenInterceptUrls = parseUrlConfig(tokenInterceptUrlConfig);
        // 解析Session拦截URL配置
        List<String> sessionInterceptUrls = parseUrlConfig(sessionInterceptUrlConfig);

        // 注册Session拦截器，只对配置的URL生效
        if (!sessionInterceptUrls.isEmpty()) {
            registry.addInterceptor(sessionInterceptor)
                    .addPathPatterns(sessionInterceptUrls.toArray(new String[0]));
            log.info("Session拦截器已注册，拦截URL: {}", sessionInterceptUrls);
        }

        // 注册Token鉴权拦截器：对所有URL生效，但排除已配置的sessionInterceptUrls
        if (!sessionInterceptUrls.isEmpty()) {
            registry.addInterceptor(tokenAuthInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(sessionInterceptUrls.toArray(new String[0]));
            log.info("Token鉴权拦截器已注册，拦截所有URL，排除Session拦截URL: {}", sessionInterceptUrls);
        } else {
            // 如果没有配置sessionInterceptUrls，则Token拦截器对所有URL生效
            registry.addInterceptor(tokenAuthInterceptor)
                    .addPathPatterns("/**");
            log.info("Token鉴权拦截器已注册，拦截所有URL");
        }
    }

    /**
     * 解析URL配置字符串，将逗号分隔的字符串转换为List
     */
    private List<String> parseUrlConfig(String urlConfig) {
        if (urlConfig == null || urlConfig.trim().isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(urlConfig.split(","));
    }
}
