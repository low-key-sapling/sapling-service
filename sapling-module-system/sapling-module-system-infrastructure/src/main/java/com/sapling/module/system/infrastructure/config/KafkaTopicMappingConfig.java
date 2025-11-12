package com.sapling.module.system.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 配置类，用于加载和管理Kafka主题与请求URI之间的映射关系
 * 通过配置属性前缀"kafka-topic"来绑定应用配置文件中的相关设置
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "kafka-topic")
public class KafkaTopicMappingConfig {

    /**
     * 存储请求URI与Kafka主题名称之间的映射关系
     */
    private Map<String, String> mappings;

    /**
     * 根据请求URI获取对应Kafka主题名称
     *
     * @param requestURI HTTP请求的URI
     * @return 对应的Kafka主题名称，如果不存在则返回null
     */
    public String getTopicByRequestURI(String requestURI) {
        // 规范化URI，移除所有斜杠
        String normalizedURI = requestURI.replace("/", "");
        // 尝试直接匹配
        String topic = mappings.get(requestURI);
        if (topic != null) {
            return topic;
        }
        // 如果直接匹配失败，尝试使用规范化后的URI
        return mappings.get(normalizedURI);
    }
}
