package com.sapling.framework.kafka.config;

import com.sapling.framework.kafka.core.properties.ZfKafkaProperties;
import com.sapling.framework.kafka.core.helper.KafkaProducerListener;
import com.sapling.framework.kafka.core.helper.KafkaTemplateHelper;
import com.sapling.framework.kafka.core.register.KafkaDynamicSourceRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 */
@EnableKafka
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@EnableConfigurationProperties(ZfKafkaProperties.class)
@Import({KafkaDynamicSourceRegister.class, KafkaProducerListener.class, KafkaTemplateHelper.class})
public class KafkaAutoConfiguration implements BeanFactoryAware, SmartInstantiationAwareBeanPostProcessor, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(KafkaAutoConfiguration.class);

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        beanFactory.getBean(KafkaDynamicSourceRegister.class);
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----Kafka消息队列组件【KafkaAutoConfiguration】");

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----Kafka消息队列组件【KafkaAutoConfiguration】");

    }
}
    