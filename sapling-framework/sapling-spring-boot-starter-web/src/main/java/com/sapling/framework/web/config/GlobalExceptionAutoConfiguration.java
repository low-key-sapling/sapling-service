package com.sapling.framework.web.config;

import com.sapling.framework.web.core.exception.handler.GlobalExceptionAdviceHandler;
import com.sapling.framework.web.core.exception.properties.ExceptionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author artisan
 * @Description: 全局异常捕获自动化配置类
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ExceptionProperties.class)
@ConditionalOnProperty(prefix = ExceptionProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GlobalExceptionAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAutoConfiguration.class);

    /**
     * 异常抛出拦截bean初始化
     *
     * @return
     */
    @Bean
    public GlobalExceptionAdviceHandler exceptionAdviceHandler() {
        return new GlobalExceptionAdviceHandler();
    }


    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----全局异常捕获组件【GlobalExceptionAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----全局异常捕获组件【GlobalExceptionAutoConfiguration】");
    }
}
