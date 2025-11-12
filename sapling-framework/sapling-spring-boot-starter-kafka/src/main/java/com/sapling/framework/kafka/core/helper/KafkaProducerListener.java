package com.sapling.framework.kafka.core.helper;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

/**
 * @author 小工匠
 * @version 1.0
 * @Description: kafka生产者监听器
 * @mark: show me the code , change the world
 */

@Component
public class KafkaProducerListener<K, V> implements ProducerListener<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerListener.class);

    /**
     * kafka发送成功回调
     *
     * @param producerRecord 记录
     * @param recordMetadata 源数据
     */
    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        if (logger.isDebugEnabled()) {
            logger.debug("key:[{}], topic:[{}], message:[{}], partition:[{}], result: [Send message to kafka success.]", producerRecord.key(), producerRecord.topic(), producerRecord.value(), producerRecord.partition());
        }
    }

    /**
     * kafka发送失败回调
     *
     * @param producerRecord 记录
     * @param recordMetadata 源数据
     * @param exception      异常
     */
    @Override
    public void onError(ProducerRecord producerRecord, RecordMetadata recordMetadata, Exception exception) {
        if (logger.isDebugEnabled()) {
            logger.debug("key:[{}], topic:[{}], message:[{}], partition:[{}], result: [Send message to kafka failed.]", producerRecord.key(), producerRecord.topic(), producerRecord.value(), producerRecord.partition(), exception);
        }
    }

}
