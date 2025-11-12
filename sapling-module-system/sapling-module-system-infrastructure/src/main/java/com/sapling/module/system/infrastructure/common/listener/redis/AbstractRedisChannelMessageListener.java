package com.sapling.module.system.infrastructure.common.listener.redis;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 基于Channel的集群消费监听器
 *
 * @author xubaoyou
 * @date 2025/4/17
 * @since V1.0
 **/
public abstract class AbstractRedisChannelMessageListener<T> implements MessageListener {
    private final ResolvableType resolvableType;

    public AbstractRedisChannelMessageListener() {
        this.resolvableType = ResolvableType.forClass(getClass()).getSuperType().getGeneric(0);
    }

    public abstract String getChannel();

    public abstract void handle(T t);

    @Override
    @SuppressWarnings("unchecked")
    public void onMessage(Message message, byte[] pattern) {
        byte[] body = message.getBody();
        String messageBody = new String(body, CharsetUtil.CHARSET_UTF_8);

        Class<?> targetType = resolvableType.resolve();
        T t;
        // 对 String 类型做特殊处理
        if (String.class.equals(targetType)) {
            t = (T) messageBody;
        } else {
            t = (T) JSONUtil.toBean(messageBody, targetType);
        }
        this.handle(t);
    }
}
