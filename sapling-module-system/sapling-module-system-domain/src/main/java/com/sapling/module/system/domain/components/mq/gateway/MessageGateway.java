package com.sapling.module.system.domain.components.mq.gateway;

import com.sapling.module.system.client.components.mq.dto.BusMessage;

public interface MessageGateway {

    <T> boolean sendMessage(BusMessage<T> message);

}
