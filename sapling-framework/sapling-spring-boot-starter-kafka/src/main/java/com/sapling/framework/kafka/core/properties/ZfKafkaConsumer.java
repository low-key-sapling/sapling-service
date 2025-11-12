package com.sapling.framework.kafka.core.properties;

import lombok.Data;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Arrays;
import java.util.List;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 */
@Data
public class ZfKafkaConsumer {

    /**
     * Concurrent Kafka Listener Container Factory
     */
    private String containerFactory;

    /**
     * Kafka消费服务器
     */
    private List<String> bootstrapServers = Arrays.asList("localhost:9092");

    /**
     * Key的反序列化，二进制的消息Key转换成具体的类型
     */
    private Class<?> keyDeserializer = StringSerializer.class;

    /**
     * Value的反序列化，二进制的消息内容转换成具体的类型
     */
    private Class<?> valueDeserializer = StringSerializer.class;

    /**
     * 标识消费者的消费组
     */
    private String groupId = "zf-base-mq";

    /**
     * 心跳与消费者协调的间隔时间
     */
    private Integer heartbeatInterval = 3000;

    /**
     * 每次fetch请求时，server应该返回的最小字节数。如果没有足够的数据返回，请求会等待，直到足够的数据才会返回。默认 1
     */
    private Integer fetchMinSize = 1;

    /**
     * Fetch请求发给broker后，在broker中可能会被阻塞的（当topic中records的总size小于fetch.min.bytes时），此时这个fetch请求耗时就会比较长。这个配置就是来配置consumer最多等待response多久。
     */
    private Integer fetchMaxWait = 100;

    /**
     * 需要在session.timeout.ms这个时间内处理完的条数 默认500
     */
    private Integer maxPollRecords = 500;

    /**
     * 自动同步offset 默认true
     */
    private Boolean enableAutoCommit = Boolean.TRUE;

    /**
     * 会话的超时限制
     */
    private Integer sessionTimeoutMs = 6000;

    /**
     * 没有初始化的offset 消费 earliest latest none 默认latest
     */
    private String autoOffsetReset = "latest";

}

    