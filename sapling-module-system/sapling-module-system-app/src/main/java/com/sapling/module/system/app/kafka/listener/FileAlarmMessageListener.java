package com.sapling.module.system.app.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.sapling.module.system.app.biz.dataTransform.component.FileAlarmMessageService;
import com.sapling.module.system.infrastructure.common.constants.KafkaTopicConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import org.springframework.stereotype.Component;

import java.util.List;


/**
 * MBWS文件判断Kafka消息监听器
 *
 * @author mbws
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileAlarmMessageListener {

    private final FileAlarmMessageService mbwsFileAlarmMessageService;


    /**
     * 监听MBWS文件判断主题消息
     *
     * @param records         消息记录
     * @param acknowledgment 手动确认
     */
    @KafkaListener(topics = KafkaTopicConstants.MBWS_FILE_ALARM_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(List<ConsumerRecord<String, String>> records,
                          Acknowledgment acknowledgment) {

        if (records == null || records.isEmpty()) {
            return;
        }

        log.info("接收到MBWS文件报警批量消息 - size: {}", records.size());

        try {
            records.forEach(record -> {
                log.info("处理MBWS文件报警消息 - Topic: {}, Partition: {}, Offset: {}, Key: {}",
                        record.topic(), record.partition(), record.offset(), record.key());
                mbwsFileAlarmMessageService.processFileAlarmMessage(record.value());
            });

            acknowledgment.acknowledge();
            ConsumerRecord<String, String> last = records.get(records.size() - 1);
            log.info("MBWS文件报警批量消息处理成功 - 最后提交偏移 Topic: {}, Partition: {}, Offset: {}",
                    last.topic(), last.partition(), last.offset());

        } catch (Exception e) {
            ConsumerRecord<String, String> first = records.get(0);
            log.error("MBWS文件报警批量消息处理失败 - 首条消息 Topic: {}, Partition: {}, Offset: {}，异常: {}",
                    first.topic(), first.partition(), first.offset(), e.getMessage(), e);
        }
    }
}