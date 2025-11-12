package com.sapling.framework.elasticsearch.config;

import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

/**
 * @author 小工匠
 * @version 1.0
 * @date 2022/4/4 23:26
 * @mark: show me the code , change the world
 */

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 2)
@EnableConfigurationProperties(BBossProperties.class)
@ConditionalOnProperty(prefix = BBossProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class BBossElasticAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(BBossElasticAutoConfiguration.class);

    private BBossESStarter bbossESStarter;

    public BBossElasticAutoConfiguration(BBossESStarter bbossESStarter) {
        this.bbossESStarter = bbossESStarter;
    }

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----ElasticSearch数据源组件【BBossElasticAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----ElasticSearch数据源组件【BBossElasticAutoConfiguration】");
    }
}
    