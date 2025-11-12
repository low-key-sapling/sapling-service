package com.sapling.module.system.infrastructure.config;

import cn.hutool.core.util.ObjectUtil;
import com.sapling.module.system.infrastructure.common.framework.properties.RedisProperties;
import com.sapling.module.system.infrastructure.common.listener.redis.AbstractRedisChannelMessageListener;
import com.sapling.module.system.infrastructure.common.listener.redis.AbstractRedisListMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis配置类
 * 提供RedisTemplate、消息监听容器和Redisson客户端的配置
 * @author mbws
 */
@Slf4j
@Configuration
@Import(RedisProperties.class)
public class RedisConfig {
    @Autowired
    private RedisProperties redisProperties;

    @Resource(name = "redisListMessageListenerExecutor")
    protected ThreadPoolTaskExecutor redisListMessageListenerExecutor;

    /**
     * 配置RedisTemplate
     * 使用StringRedisSerializer序列化key
     * 使用GenericJackson2JsonRedisSerializer序列化value
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置Redis消息监听容器
     * 自动注册所有AbstractRedisChannelMessageListener类型的监听器
     */
    @Bean
    @SuppressWarnings("rawtypes")
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisTemplate<String, Object> redisTemplate, 
            List<MessageListener> listeners) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getRequiredConnectionFactory());

        listeners.stream()
                .filter(listener -> listener instanceof AbstractRedisChannelMessageListener)
                .map(listener -> (AbstractRedisChannelMessageListener) listener)
                .forEach(listener -> {
                    String channel = listener.getChannel();
                    if (ObjectUtil.isEmpty(channel)) {
                        log.warn("类{}尚未配置Channel", listener.getClass().getName());
                        return;
                    }
                    log.info("开启监听channel:{}", channel);
                    container.addMessageListener(listener, new ChannelTopic(channel));
                });

        return container;
    }

    /**
     * 配置Redisson客户端
     * 支持单机、哨兵、集群三种模式
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        configureRedissonMode(config);
        configureRedissonCommon(config);
        return Redisson.create(config);
    }

    private void configureRedissonMode(Config config) {
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        if (ObjectUtil.isEmpty(cluster) || ObjectUtil.isEmpty(cluster.getNodes())) {
            configureSingleMode(config);
        } else {
            configureClusterMode(config);
        }
    }

    private void configureClusterMode(Config config) {
        ClusterServersConfig clusterConfig = config.useClusterServers();
        // 禁用槽位检查
        clusterConfig.setCheckSlotsCoverage(false);
        redisProperties.getCluster().getNodes().forEach(node ->
                clusterConfig.addNodeAddress("redis://" + node.trim())
        );
        setPasswordIfNeeded(clusterConfig);
    }

    private void configureSentinelMode(Config config) {
        SentinelServersConfig sentinelConfig = config.useSentinelServers();
        sentinelConfig.setMasterName(redisProperties.getSentinel().getMaster());
        redisProperties.getSentinel().getNodes().forEach(node ->
                sentinelConfig.addSentinelAddress("redis://" + node.trim())
        );
        setPasswordIfNeeded(sentinelConfig);
    }

    private void configureSingleMode(Config config) {
        SingleServerConfig singleConfig = config.useSingleServer();
        singleConfig.setAddress(String.format("redis://%s:%s",
                redisProperties.getHost(),
                redisProperties.getPort()
        ));
        setPasswordIfNeeded(singleConfig);
    }

    private void setPasswordIfNeeded(BaseConfig<?> config) {
        if (StringUtils.hasText(redisProperties.getPassword())) {
            config.setPassword(redisProperties.getPassword());
        }
    }

    private void configureRedissonCommon(Config config) {
        config.setThreads(Runtime.getRuntime().availableProcessors() * 2);
        config.setTransportMode(TransportMode.NIO);
    }

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<AbstractRedisListMessageListener> redisListMessageListener(RedisTemplate<String, String> redisTemplate, List<AbstractRedisListMessageListener> listeners) {
        listeners.forEach(listener -> redisListMessageListenerExecutor.execute(() -> {
            String listName = listener.getListName();
            if (ObjectUtil.isEmpty(listName)) {
                log.error("存在Redis List名称为空的监听器，监听器类为【{}】", listener.getClass().getName());
                return;
            }
            log.info("开启监听Redis List【{}】", listName);

            while (true) {
                try {
                    String message = redisTemplate.opsForList().rightPop(listName, 30, TimeUnit.SECONDS);
                    if (ObjectUtil.isEmpty(message)) {
                        continue;
                    }
                    listener.onMessage(message);
                } catch (Exception e) {
                    log.error("监听Redis List【{}】出现异常", listName, e);
                    try {
                        TimeUnit.SECONDS.sleep(30);
                    } catch (InterruptedException ex) {
                        log.error("监听Redis List【{}】出现异常", listName, e);
                    }
                }
            }
        }));

        return listeners;
    }
}
