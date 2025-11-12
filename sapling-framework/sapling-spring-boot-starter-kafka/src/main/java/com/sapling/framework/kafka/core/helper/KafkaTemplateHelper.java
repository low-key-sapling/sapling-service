package com.sapling.framework.kafka.core.helper;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ExecutionException;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 */
@Component
public class KafkaTemplateHelper<K, V> {

    /**
     * 同步发送消息
     *
     * @param kafkaTemplate    kafka template
     * @param topic            topic
     * @param message          message
     * @param producerListener producer listener
     * @return 发送结果
     */
    public Boolean syncSend(KafkaTemplate<K, V> kafkaTemplate, String topic, V message, ProducerListener<K, V> producerListener) {
        Assert.notNull(ObjectUtils.isNotEmpty(topic), "Send message to kafka topic cannot be empty.");
        Assert.notNull(ObjectUtils.isNotEmpty(message), "Send message to kafka data cannot be empty.");
        try {
            if (ObjectUtils.isNotEmpty(producerListener)) {
                kafkaTemplate.setProducerListener(producerListener);
            }
            kafkaTemplate.send(topic, message).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Send message to Kafka failed.", e);
        }
        return true;
    }

    /**
     * 同步发送消息
     *
     * @param kafkaTemplate kafka template
     * @param topic         topic
     * @param message       消息
     * @return 发送结果
     */
    public Boolean syncSend(KafkaTemplate<K, V> kafkaTemplate, String topic, V message) {
        return syncSend(kafkaTemplate, topic, message, null);
    }

    /**
     * 异步发送数据
     *
     * @param kafkaTemplate    kafka template
     * @param topic            topic
     * @param message          消息
     * @param producerListener producer listener
     */
    public void asyncSend(KafkaTemplate<K, V> kafkaTemplate, String topic, V message, ProducerListener<K, V> producerListener) {
        Assert.notNull(ObjectUtils.isNotEmpty(topic), "Send message to kafka topic cannot be empty.");
        Assert.notNull(ObjectUtils.isNotEmpty(message), "Send message to kafka data cannot be empty.");
        if (ObjectUtils.isNotEmpty(producerListener)) {
            kafkaTemplate.setProducerListener(producerListener);
        }
        kafkaTemplate.send(topic, message);
    }

    /**
     * 异步发送数据
     *
     * @param kafkaTemplate kafka template
     * @param topic         topic
     * @param message       消息
     */
    public void asyncSend(KafkaTemplate<K, V> kafkaTemplate, String topic, V message) {
        asyncSend(kafkaTemplate, topic, message, null);
    }

    /**
     * 同步发送消息到指定key对应的分区
     *
     * @param kafkaTemplate    kafka template
     * @param topic            topic
     * @param key              key
     * @param message          message
     * @param producerListener producer listener
     * @return 发送结果
     */
    public Boolean syncSendWithKey(KafkaTemplate<K, V> kafkaTemplate, String topic, K key, V message, ProducerListener<K, V> producerListener) {
        Assert.notNull(ObjectUtils.isNotEmpty(topic), "Send message to kafka topic cannot be empty.");
        Assert.notNull(ObjectUtils.isNotEmpty(message), "Send message to kafka data cannot be empty.");
        Assert.notNull(ObjectUtils.isNotEmpty(key), "Send message to kafka key cannot be empty.");
        try {
            if (ObjectUtils.isNotEmpty(producerListener)) {
                kafkaTemplate.setProducerListener(producerListener);
            }
            kafkaTemplate.send(topic, key, message).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Send message to Kafka failed.", e);
        }
        return true;
    }

    /**
     * 同步发送消息到指定key对应的分区
     *
     * @param kafkaTemplate kafka template
     * @param topic         topic
     * @param key           key
     * @param message       消息
     * @return 发送结果
     */
    public Boolean syncSendWithKey(KafkaTemplate<K, V> kafkaTemplate, String topic, K key, V message) {
        return syncSendWithKey(kafkaTemplate, topic, key, message, null);
    }

    /**
     * 异步发送消息到指定key对应的分区
     *
     * @param kafkaTemplate    kafka template
     * @param topic            topic
     * @param key              key
     * @param message          消息
     * @param producerListener producer listener
     */
    public void asyncSendWithKey(KafkaTemplate<K, V> kafkaTemplate, String topic, K key, V message, ProducerListener<K, V> producerListener) {
        Assert.notNull(ObjectUtils.isNotEmpty(topic), "Send message to kafka topic cannot be empty.");
        Assert.notNull(ObjectUtils.isNotEmpty(message), "Send message to kafka data cannot be empty.");
        Assert.notNull(ObjectUtils.isNotEmpty(key), "Send message to kafka key cannot be empty.");
        if (ObjectUtils.isNotEmpty(producerListener)) {
            kafkaTemplate.setProducerListener(producerListener);
        }
        kafkaTemplate.send(topic, key, message);
    }

    /**
     * 异步发送消息到指定key对应的分区
     *
     * @param kafkaTemplate kafka template
     * @param topic         topic
     * @param key           key
     * @param message       消息
     */
    public void asyncSendWithKey(KafkaTemplate<K, V> kafkaTemplate, String topic, K key, V message) {
        asyncSendWithKey(kafkaTemplate, topic, key, message, null);
    }

}
