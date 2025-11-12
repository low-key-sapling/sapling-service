package com.sapling.module.system.client.components.mq;

import com.sapling.module.system.client.components.mq.dto.BusMessage;

public interface IMessageService {

    /**
     * 发送消息
     *
     * @param message 消息对象
     * @return 是否发送成功
     */
    <T> boolean sendMessage(BusMessage<T> message);

}

