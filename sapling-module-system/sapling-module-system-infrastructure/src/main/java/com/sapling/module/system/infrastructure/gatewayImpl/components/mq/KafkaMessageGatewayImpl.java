package com.sapling.module.system.infrastructure.gatewayImpl.components.mq;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sapling.framework.kafka.core.helper.KafkaTemplateHelper;
import com.sapling.module.system.client.components.mq.dto.BusMessage;
import com.sapling.module.system.domain.components.mq.gateway.MessageGateway;
import com.sapling.module.system.infrastructure.common.constants.KafkaTopicConstants;
import com.sapling.module.system.infrastructure.config.KafkaTopicMappingConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Kafka消息发送服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageGatewayImpl implements MessageGateway {

    @Resource
    private KafkaTopicMappingConfig kafkaTopicMappingConfig;


    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplateHelper<String, String> kafkaTemplateHelper;


    @Override
    public <T> boolean sendMessage(BusMessage<T> message) {
        // JUST DEMO
        try {
            // 根据REQUEST选择不同的topic
            String topic = getTopic(message);
            // 使用KafkaTemplateHelper发送消息    以deviceId为分区key  partition key
            return kafkaTemplateHelper.syncSendWithKey(kafkaTemplate, topic, message.getDeviceId(), message.busMessageToJson());
        } catch (Exception e) {
            log.error("kafkaMessage: [{}] send failed, reason [{}]",message.busMessageToJson(), ExceptionUtil.getMessage(e));
            return false;
        }
    }

    /**
     * 根据消息的请求URI获取对应的主题名称。
     * 
     * @param <T> 消息数据的类型
     * @param message 包含请求URI和数据的消息对象
     * @return 返回与请求URI关联的主题名称，若未找到则返回默认主题
     */
    private <T> String getTopic(BusMessage<T> message) {
        String topic = kafkaTopicMappingConfig.getTopicByRequestURI(message.getRequestURI());
        topic = (topic  == null)  ? KafkaTopicConstants.DEFAULT_TOPIC : topic;
        return topic;
    }
}