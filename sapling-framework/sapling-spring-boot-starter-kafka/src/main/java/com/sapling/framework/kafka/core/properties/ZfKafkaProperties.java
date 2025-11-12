package com.sapling.framework.kafka.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 */
@Data
@Configuration
@ConfigurationProperties("zf.kafka.dynamic")
public class ZfKafkaProperties {

    /**
     * 主数据源
     */
    private String primary;

    /**
     * Kafka消费服务器
     */
    private List<String> bootstrapServers;

    /**
     * 消费者
     */
    @NestedConfigurationProperty
    private ZfKafkaConsumer consumer;

    /**
     * 生产者
     */
    @NestedConfigurationProperty
    private ZfKafkaProducer producer;

    /**
     * 安全配置
     * 支持 SASL/PLAIN 认证和 SSL 加密
     */
    @NestedConfigurationProperty
    private ZfKafkaSecurity security;

    /**
     * 数据源
     */
    private Map<String, ZfKafkaProperties> datasource;

}
    