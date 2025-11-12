package com.sapling.module.system.infrastructure.common.listener.redis;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis List队列监听器
 */
@Slf4j
public abstract class AbstractRedisListMessageListener {
    public abstract String getListName();

    public abstract void handle(String message);

    public void onMessage(String message) {
        try {
            this.handle(message);
        } catch (Exception e) {
            log.error("Redis List消息处理异常", e);
        }
    }
}
