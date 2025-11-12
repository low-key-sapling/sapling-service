package com.sapling.framework.kafka.core.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 安全配置类
 * 支持 SASL/PLAIN 认证机制
 *
 * @author 小工匠
 * @version 1.0
 */
@Slf4j
@Data
public class ZfKafkaSecurity {

    /**
     * 安全协议类型
     * 支持: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL
     */
    private String protocol;

    /**
     * SASL 认证机制
     * 支持: PLAIN, SCRAM-SHA-256, SCRAM-SHA-512
     */
    private String mechanism;

    /**
     * SASL 认证用户名
     */
    private String username;

    /**
     * SASL 认证密码
     */
    private String password;

    /**
     * SSL 信任存储位置
     * 支持 classpath: 和 file: 前缀
     */
    private String trustStoreLocation;

    /**
     * SSL 信任存储密码
     */
    private String trustStorePassword;

    /**
     * 其他安全相关属性
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * 判断是否启用了安全配置
     *
     * @return true 如果配置了安全协议
     */
    public boolean isEnabled() {
        return StringUtils.isNotBlank(protocol);
    }

    /**
     * 判断是否启用了 SASL 认证
     *
     * @return true 如果协议包含 SASL
     */
    public boolean isSaslEnabled() {
        return isEnabled() && (protocol.contains("SASL") || protocol.contains("sasl"));
    }

    /**
     * 判断是否启用了 SSL
     *
     * @return true 如果协议包含 SSL
     */
    public boolean isSslEnabled() {
        return isEnabled() && (protocol.contains("SSL") || protocol.contains("ssl"));
    }

    /**
     * 构建 JAAS 配置字符串
     * 当前仅支持 PLAIN 机制
     *
     * @return JAAS 配置字符串
     * @throws UnsupportedOperationException 如果机制不是 PLAIN
     */
    public String buildJaasConfig() {
        if (!"PLAIN".equalsIgnoreCase(mechanism)) {
            throw new UnsupportedOperationException(
                    "Only PLAIN mechanism is currently supported, but got: " + mechanism);
        }

        return String.format(
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"%s\" password=\"%s\";",
                username, password
        );
    }

    /**
     * 验证安全配置的完整性
     *
     * @throws IllegalArgumentException 如果配置不完整或无效
     */
    public void validate() {
        if (!isEnabled()) {
            return;
        }

        // 验证 SASL 配置
        if (isSaslEnabled()) {
            Assert.hasText(mechanism, "SASL mechanism must be specified when using SASL protocol");

            if ("PLAIN".equalsIgnoreCase(mechanism)) {
                Assert.hasText(username, "Username is required for SASL/PLAIN authentication");
                Assert.hasText(password, "Password is required for SASL/PLAIN authentication");
            }
        }

        // 验证 SSL 配置
        if (isSslEnabled() && StringUtils.isBlank(trustStoreLocation)) {
            log.warn("SSL is enabled but trust store location is not configured. " +
                    "This may cause connection failures.");
        }
    }
}
