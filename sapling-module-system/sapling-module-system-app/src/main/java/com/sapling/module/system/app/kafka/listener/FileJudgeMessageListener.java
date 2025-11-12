package com.sapling.module.system.app.kafka.listener;

import com.sapling.module.system.app.biz.dataTransform.component.FileJudgeMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.infrastructure.common.constants.KafkaTopicConstants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * MBWS文件判断Kafka消息监听器
 *
 * @author mbws
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileJudgeMessageListener {

    private final FileJudgeMessageService mbwsFileJudgeMessageService;
    private final Executor kafkaMessageExecutor;

    /**
     * 监听MBWS文件判断主题消息
     *
     * @param message        消息内容
     * @param topic          主题名称
     * @param partition      分区
     * @param offset         偏移量
     * @param acknowledgment 手动确认
     */
    @KafkaListener(topics = KafkaTopicConstants.MBWS_FILE_JUDGE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(@Payload List<String> message,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                          @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                          @Header(KafkaHeaders.OFFSET) long offset,
                          Acknowledgment acknowledgment) {

        log.info("接收到MBWS文件判定消息 - Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);

        if (CollectionUtils.isEmpty(message)) {
            log.warn("接收到MBWS文件判定消息 - Topic: {} - message is empty", topic);
            return;
        }

        // 将消息处理任务提交到线程池
        kafkaMessageExecutor.execute(() -> {
            try {
                // 处理消息
                message.forEach(mbwsFileJudgeMessageService::processFileJudgeMessage);

                // 手动确认消息
                acknowledgment.acknowledge();

                log.info("MBWS文件判定消息处理成功 - Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);

            } catch (Exception e) {
                log.error("MBWS文件判定消息处理失败 - Topic: {}, Partition: {}, Offset: {}, Message: {}", topic, partition, offset, message, e);

                // 根据业务需求决定是否确认消息
                // 如果不确认，消息会重新投递
                acknowledgment.acknowledge();
            }
        });
    }
}