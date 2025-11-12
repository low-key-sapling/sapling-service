package com.sapling.module.system.infrastructure.common.utils.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Redis Publish Helper
 * @param <T> 发送消息泛型
 */
@Component
public class RedisPublishHelper<T> extends RedisCommonHelper {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发布消息
     * @param channel 频道名称
     * @param message 消息内容
     */
    public void publish(String channel, T message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
