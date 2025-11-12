package com.sapling.module.system.infrastructure.config;

import com.sapling.module.system.infrastructure.common.constants.RedisChannelConstants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

/**
 * Kafka配置类
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka", name = "enabled", havingValue = "true", matchIfMissing = false)
public class KafkaConfig {
    
    /**
     * 创建组织缓存更新主题
     * 将Redis频道映射到Kafka主题
     */
    @Bean
    public NewTopic orgCacheUpdateTopic() {
        // 将Redis频道名称转换为Kafka主题名称
        // 移除HashTag标记，替换冒号为下划线
        String topicName = RedisChannelConstants.ORG_CACHE_UPDATE_CHANNEL
                .replace(":", "_");
        
        return TopicBuilder.name(topicName)
                .partitions(12)  // 设置12个分区
                .replicas(1)    // 设置1个副本
                .build();
    }
    
    /**
     * 配置消息转换器，支持JSON格式
     */
    @Bean
    public RecordMessageConverter jsonConverter() {
        return new StringJsonMessageConverter();
    }
}
