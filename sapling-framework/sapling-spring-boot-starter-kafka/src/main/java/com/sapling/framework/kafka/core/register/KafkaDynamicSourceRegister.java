package com.sapling.framework.kafka.core.register;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.sapling.framework.kafka.core.properties.ZfKafkaConsumer;
import com.sapling.framework.kafka.core.properties.ZfKafkaProducer;
import com.sapling.framework.kafka.core.properties.ZfKafkaProperties;
import com.sapling.framework.kafka.core.properties.ZfKafkaSecurity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 */

@Slf4j
@Component
public class KafkaDynamicSourceRegister implements InitializingBean {

    @Resource
    private ZfKafkaProperties zfKafkaProperties;

    @Resource
    private DefaultListableBeanFactory beanFactory;

    /**
     * 应用安全配置到 Kafka 配置映射
     *
     * @param config   Kafka 配置映射
     * @param security 安全配置
     */
    private void applySecurity(Map<String, Object> config, ZfKafkaSecurity security) {
        if (security == null || !security.isEnabled()) {
            return;
        }

        // 验证安全配置
        security.validate();

        // 添加安全协议
        config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, security.getProtocol());
        log.debug("Applied security protocol: {}", security.getProtocol());

        // 添加 SASL 配置
        if (security.isSaslEnabled()) {
            config.put(SaslConfigs.SASL_MECHANISM, security.getMechanism());
            config.put(SaslConfigs.SASL_JAAS_CONFIG, security.buildJaasConfig());
            log.debug("Applied SASL mechanism: {}", security.getMechanism());
        }

        // 添加 SSL 配置
        if (security.isSslEnabled()) {
            if (StringUtils.isNotBlank(security.getTrustStoreLocation())) {
                config.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, security.getTrustStoreLocation());
                log.debug("Applied SSL trust store location: {}", security.getTrustStoreLocation());
            }
            if (StringUtils.isNotBlank(security.getTrustStorePassword())) {
                config.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, security.getTrustStorePassword());
                log.debug("Applied SSL trust store password: [PROTECTED]");
            }
        }

        // 添加其他安全属性
        if (security.getProperties() != null && !security.getProperties().isEmpty()) {
            config.putAll(security.getProperties());
            log.debug("Applied {} additional security properties", security.getProperties().size());
        }
    }

    /**
     * 生产者配置
     *
     * @param producer 生产者配置
     * @param security 安全配置
     * @return 配置信息
     */
    private Map<String, Object> producerConfig(ZfKafkaProducer producer, ZfKafkaSecurity security) {
        Map<String, Object> producerConfigs = new HashMap<>(16);
        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producer.getBootstrapServers());
        producerConfigs.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
        producerConfigs.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, producer.getMaxRequestSize());
        producerConfigs.put(ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
        producerConfigs.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producer.getBufferMemory());
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // 应用安全配置
        applySecurity(producerConfigs, security);

        return producerConfigs;
    }


    /**
     * 消费者配置
     *
     * @param consumer 消费者配置
     * @param security 安全配置
     * @return 配置信息
     */
    private Map<String, Object> consumerConfig(ZfKafkaConsumer consumer, ZfKafkaSecurity security) {
        Map<String, Object> consumerConfigs = new HashMap<>(16);
        consumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumer.getBootstrapServers());
        consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, consumer.getGroupId());
        consumerConfigs.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, consumer.getHeartbeatInterval());
        consumerConfigs.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, consumer.getFetchMinSize());
        consumerConfigs.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, consumer.getFetchMaxWait());
        consumerConfigs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumer.getMaxPollRecords());
        consumerConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumer.getEnableAutoCommit());
        consumerConfigs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumer.getSessionTimeoutMs());
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumer.getAutoOffsetReset());
        consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 应用安全配置
        applySecurity(consumerConfigs, security);

        return consumerConfigs;
    }


    @Override
    public void afterPropertiesSet() {
        if (ObjectUtils.isEmpty(zfKafkaProperties) || ObjectUtils.isEmpty(zfKafkaProperties.getDatasource())) {
            return;
        }
        Assert.isTrue(StringUtils.isNotBlank(zfKafkaProperties.getPrimary()), "kafka not setting primary datasource.");

        zfKafkaProperties.getDatasource().forEach((datasourceName, datasource) -> {
            datasource = getDataSource(datasource);
            if (ObjectUtils.isEmpty(datasource)) {
                return;
            }
            registerKafkaTemplate(datasourceName, datasource);
            registerContainerFactory(datasourceName, datasource);
        });
        log.info("kafka-multiple-datasource initial loaded [{}] datasource, primary datasource named [{}]", zfKafkaProperties.getDatasource().size(), zfKafkaProperties.getPrimary());
    }

    /**
     * kafka template register
     *
     * @param datasourceName 数据源名称
     * @param datasource     数据源
     */
    private void registerKafkaTemplate(String datasourceName, ZfKafkaProperties datasource) {
        if (ObjectUtils.isEmpty(datasource.getProducer())) {
            return;
        }
        Assert.isTrue(StringUtils.isNotBlank(datasource.getProducer().getKafkaTemplate()), "kafka-template is null not allowed.");
        
        // 传递安全配置到生产者配置
        DefaultKafkaProducerFactory<Object, Object> producerFactory = new DefaultKafkaProducerFactory<>(
                producerConfig(datasource.getProducer(), datasource.getSecurity()));
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(DefaultKafkaProducerFactory.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        if (Objects.equals(zfKafkaProperties.getPrimary(), datasourceName)) {
            beanDefinition.setPrimary(true);
        }
        if (ObjectUtils.isEmpty(beanFactory.getSingleton(datasource.getProducer().getKafkaTemplate()))) {
            beanFactory.registerSingleton(datasource.getProducer().getKafkaTemplate(), kafkaTemplate);
            
            // 记录安全配置状态（不暴露敏感信息）
            if (datasource.getSecurity() != null && datasource.getSecurity().isEnabled()) {
                log.info("kafka-multiple-datasource => add a kafka template named [{}] with security protocol [{}] success.", 
                        datasource.getProducer().getKafkaTemplate(), datasource.getSecurity().getProtocol());
            } else {
                log.info("kafka-multiple-datasource => add a kafka template named [{}] success.", 
                        datasource.getProducer().getKafkaTemplate());
            }
        }
    }

    /**
     * register container factory
     *
     * @param datasourceName 数据源名称
     * @param datasource     数据源
     */
    private void registerContainerFactory(String datasourceName, ZfKafkaProperties datasource) {
        if (ObjectUtils.isEmpty(datasource.getConsumer())) {
            return;
        }
        Assert.isTrue(StringUtils.isNotBlank(datasource.getConsumer().getContainerFactory()), "concurrent-kafka-listener-container-factory is null not allowed.");
        
        // 传递安全配置到消费者配置
        DefaultKafkaConsumerFactory<Object, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerConfig(datasource.getConsumer(), datasource.getSecurity()));
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setPollTimeout(1500);
        if (!datasource.getConsumer().getEnableAutoCommit()) {
            factory.getContainerProperties().setAckMode((ContainerProperties.AckMode.MANUAL));
        }
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(DefaultKafkaConsumerFactory.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        if (Objects.equals(zfKafkaProperties.getPrimary(), datasourceName)) {
            beanDefinition.setPrimary(true);
        }
        if (ObjectUtils.isEmpty(beanFactory.getSingleton(datasource.getConsumer().getContainerFactory()))) {
            beanFactory.registerSingleton(datasource.getConsumer().getContainerFactory(), factory);
            
            // 记录安全配置状态（不暴露敏感信息）
            if (datasource.getSecurity() != null && datasource.getSecurity().isEnabled()) {
                log.info("kafka-dynamic-datasource => add a kafka listener container factory named [{}] with security protocol [{}] success.", 
                        datasource.getConsumer().getContainerFactory(), datasource.getSecurity().getProtocol());
            } else {
                log.info("kafka-dynamic-datasource => add a kafka listener container factory named [{}] success.", 
                        datasource.getConsumer().getContainerFactory());
            }
        }
    }

    /**
     * 获取数据源
     *
     * @param datasource 数据源参数
     * @return @{@link ZfKafkaProperties}
     */
    private ZfKafkaProperties getDataSource(ZfKafkaProperties datasource) {
        ZfKafkaProperties propertiesWrapper = new ZfKafkaProperties();
        BeanUtil.copyProperties(zfKafkaProperties, propertiesWrapper);
        if (ObjectUtils.isEmpty(datasource)) {
            return propertiesWrapper;
        }
        Assert.notNull(datasource.getBootstrapServers(), "kafka bootstrap servers cannot be empty.");
        if (ObjectUtils.isNotEmpty(datasource.getConsumer())) {
            datasource.getConsumer().setBootstrapServers(datasource.getBootstrapServers());
        }
        if (ObjectUtils.isNotEmpty(datasource.getProducer())) {
            datasource.getProducer().setBootstrapServers(datasource.getBootstrapServers());
        }
        BeanUtil.copyProperties(datasource.getProducer(), propertiesWrapper.getProducer(), CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        BeanUtil.copyProperties(datasource.getConsumer(), propertiesWrapper.getConsumer(), CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        
        // 合并安全配置（子级配置覆盖父级配置）
        if (ObjectUtils.isNotEmpty(datasource.getSecurity())) {
            if (propertiesWrapper.getSecurity() == null) {
                propertiesWrapper.setSecurity(new ZfKafkaSecurity());
            }
            BeanUtil.copyProperties(datasource.getSecurity(), propertiesWrapper.getSecurity(), 
                    CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        }
        
        return propertiesWrapper;
    }

}
    