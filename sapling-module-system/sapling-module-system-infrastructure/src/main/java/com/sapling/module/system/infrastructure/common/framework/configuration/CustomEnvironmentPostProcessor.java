package com.sapling.module.system.infrastructure.common.framework.configuration;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import com.sapling.framework.common.exception.BasicException;
import com.sapling.framework.common.exception.enums.AppHttpStatus;
import com.sapling.framework.common.utils.enc.JasyptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mbws
 */
@Component
@Slf4j
public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 从环境变量设置JasyptKey
        String jasyptKey = environment.getProperty("jasypt.encryptor.key");
        if (StringUtils.isNotBlank(jasyptKey)) {
            JasyptUtil.setJasyptKey(jasyptKey);
            log.info("set JasyptUtil jasyptKey success");
        }

        Map<String, Object> properties = new HashMap<>(10);

        // 数据库参数设置
        String dbUserName = environment.getProperty("spring.datasource.username");
        if (StringUtils.isNotBlank(dbUserName)) {
            // 重新设置值
            properties.put("spring.datasource.username", JasyptUtil.jasyptDecrypt4EndPoint(dbUserName));
            log.info("set spring.datasource.username success");
        }
        String dbPassword = environment.getProperty("spring.datasource.password");
        if (StringUtils.isNotBlank(dbPassword)) {
            // 重新设置值
            properties.put("spring.datasource.password", JasyptUtil.jasyptDecrypt4EndPoint(dbPassword));
            log.info("set spring.datasource.password success");
        }

        // nacos参数设置，从environment中获取属性值
        String nacosUserName = environment.getProperty("spring.cloud.nacos.discovery.username");
        if (StringUtils.isNotBlank(nacosUserName)) {
            // 重新设置值
            properties.put("spring.cloud.nacos.discovery.username", JasyptUtil.jasyptDecrypt4EndPoint(nacosUserName));
            log.info("set spring.cloud.nacos.discovery.username success");
        }
        String nacosPassword = environment.getProperty("spring.cloud.nacos.discovery.password");
        if (StringUtils.isNotBlank(nacosPassword)) {
            properties.put("spring.cloud.nacos.discovery.password", JasyptUtil.jasyptDecrypt4EndPoint(nacosPassword));
            log.info("set spring.cloud.nacos.discovery.password success");
        }

        // redis参数设置,从environment中获取属性值
        String redisPwd = environment.getProperty("spring.redis.password");
        if (StringUtils.isNotBlank(redisPwd)) {
            // 重新设置值
            properties.put("spring.redis.password", JasyptUtil.jasyptDecrypt4EndPoint(redisPwd));
            log.info("set spring.redis.password success");
        }

        // es参数设置,从environment中获取属性值
        String esUserName = environment.getProperty("spring.elasticsearch.bboss.elasticUser");
        if (StringUtils.isNotBlank(esUserName)) {
            // 重新设置值
            properties.put("spring.elasticsearch.bboss.elasticUser", JasyptUtil.jasyptDecrypt4EndPoint(esUserName));
            log.info("set spring.elasticsearch.bboss.elasticUser success");
        }

        String esPwd = environment.getProperty("spring.elasticsearch.bboss.elasticPassword");
        if (StringUtils.isNotBlank(esPwd)) {
            // 重新设置值
            properties.put("spring.elasticsearch.bboss.elasticPassword", JasyptUtil.jasyptDecrypt4EndPoint(esPwd));
            log.info("set spring.elasticsearch.bboss.elasticPassword success");
        }


        //Kafka 参数设置
        String kafkaTurstStorePassword = environment.getProperty("spring.kafka.ssl.trust-store-password");
        if (StringUtils.isNotBlank(kafkaTurstStorePassword)) {
            // 重新设置值
            properties.put("spring.kafka.ssl.trust-store-password", JasyptUtil.jasyptDecrypt4EndPoint(kafkaTurstStorePassword));
            log.info("set spring.kafka.ssl.trust-store-password success");
        }

        String kafkaJaasConfig = environment.getProperty("spring.kafka.properties.sasl.jaas.config");
        if (StringUtils.isNotBlank(kafkaJaasConfig)) {
            // 重新设置值
            properties.put("spring.kafka.properties.sasl.jaas.config", JasyptUtil.jasyptDecrypt4EndPoint(kafkaJaasConfig));
            log.info("set spring.kafka.properties.sasl.jaas.config success");
        }



        //Windows系统暂时移除关于kafka的认证
//        if (System.getProperty("os.name").toLowerCase().contains("win")) {
//            properties.put("spring.kafka.ssl.trust-store-location","");
//            properties.put("spring.kafka.ssl.trust-store-password","");
//            properties.put("spring.kafka.security.protocol", "PLAINTEXT");
//            properties.put("spring.kafka.properties.sasl.mechanism","");
//            properties.put("spring.kafka.properties.sasl.jaas.config","");
//            properties.put("spring.kafka.properties.ssl.endpoint.identification.algorithm","");
//        }

        // monitor
        String monitorUserName = environment.getProperty("ko-time.user-name");
        if (StringUtils.isNotBlank(monitorUserName)) {
            // 重新设置值
            properties.put("ko-time.user-name", JasyptUtil.jasyptDecrypt4EndPoint(monitorUserName));
            log.info("set ko-time.user-name success");
        }

        String monitorPwd = environment.getProperty("ko-time.password");
        if (StringUtils.isNotBlank(monitorPwd)) {
            // 重新设置值
            properties.put("ko-time.password", JasyptUtil.jasyptDecrypt4EndPoint(monitorPwd));
            log.info("set ko-time.password success");
        }

        // 处理Redis配置
        handleRedisConfig(environment, properties);

        // 加载到环境变量中
        environment.getPropertySources().addFirst(new MapPropertySource("customProperties", properties));
    }

    private void handleRedisConfig(ConfigurableEnvironment environment, Map<String, Object> properties) {
        // 此处的Redis配置是一个非标准的Redis配置，目的是兼容uom配置集群和单机的redis节点
        String redisNodes = environment.getProperty("spring.redis.nodes");
        if (ObjectUtil.isEmpty(redisNodes)) {
            log.error("！！！！！！！！！！！Redis尚未配置！！！！！！！！！！！！！");
            throw new BasicException(AppHttpStatus.REDIS_EXCEPTION);
        }
        if (redisNodes.contains(";")) {
            // 集群配置
            String redisMode = environment.getProperty("spring.redis.mode", "cluster");
            properties.put("spring.redis." + redisMode + ".nodes", redisNodes.replace(";", ","));
        } else {
            // 单机配置
            String[] redisNodeArr = redisNodes.split(":");
            properties.put("spring.redis.host", redisNodeArr[0]);
            properties.put("spring.redis.port", redisNodeArr[1]);
        }
    }
}
