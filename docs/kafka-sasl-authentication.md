# Kafka SASL/PLAIN 认证配置指南

## 概述

本文档描述了如何在 `endpoint-spring-boot-starter-kafka` 模块中配置 SASL/PLAIN 认证，以安全地连接到需要认证的 Kafka 集群。该功能支持多数据源配置，每个数据源可以有独立的认证设置。

## 功能特性

- **SASL/PLAIN 认证**: 支持基于用户名/密码的简单认证机制
- **多协议支持**: 支持 SASL_PLAINTEXT（无加密）和 SASL_SSL（带加密）
- **多数据源**: 每个 Kafka 数据源可以有独立的安全配置
- **向后兼容**: 现有的非认证配置无需修改即可继续使用
- **灵活配置**: 支持环境变量、加密属性等多种配置方式

## 配置属性说明

### 安全配置属性

在 `zf.kafka.dynamic.datasource.[name].security` 命名空间下配置：

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `protocol` | String | 是 | 安全协议类型：PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL |
| `mechanism` | String | 条件 | SASL 机制类型：PLAIN（当前仅支持 PLAIN） |
| `username` | String | 条件 | SASL 认证用户名（SASL 启用时必填） |
| `password` | String | 条件 | SASL 认证密码（SASL 启用时必填） |
| `trust-store-location` | String | 否 | SSL 信任存储文件路径（SSL 启用时建议配置） |
| `trust-store-password` | String | 否 | SSL 信任存储密码 |
| `properties` | Map | 否 | 其他安全相关属性 |

### 协议类型说明

- **PLAINTEXT**: 无加密无认证（默认，不安全）
- **SSL**: 仅 SSL 加密，无认证
- **SASL_PLAINTEXT**: SASL 认证但无 SSL 加密（适用于内网环境）
- **SASL_SSL**: SASL 认证 + SSL 加密（推荐生产环境使用）

## 配置示例

### 示例 1: SASL_PLAINTEXT 认证（无 SSL）

适用于内网环境，不需要 SSL 加密但需要认证：

```yaml
zf:
  kafka:
    dynamic:
      primary: dev-kafka
      datasource:
        dev-kafka:
          bootstrap-servers: kafka-dev.example.com:9092
          # 安全配置
          security:
            protocol: SASL_PLAINTEXT
            mechanism: PLAIN
            username: dev-user
            password: dev-password
          consumer:
            container-factory: devKafkaListenerContainerFactory
            group-id: dev-group
            auto-offset-reset: latest
          producer:
            kafka-template: devKafkaTemplate
```

### 示例 2: SASL_SSL 认证（带 SSL 加密）

适用于生产环境，同时需要认证和加密：

```yaml
zf:
  kafka:
    dynamic:
      primary: prod-kafka
      datasource:
        prod-kafka:
          bootstrap-servers: kafka-prod.example.com:9093
          # 安全配置
          security:
            protocol: SASL_SSL
            mechanism: PLAIN
            username: ${KAFKA_USERNAME}  # 从环境变量读取
            password: ${KAFKA_PASSWORD}  # 从环境变量读取
            trust-store-location: file:./jks/client_truststore.jks
            trust-store-password: ${TRUSTSTORE_PASSWORD}
            # 其他 SSL 属性
            properties:
              ssl.endpoint.identification.algorithm: ""
              request.timeout.ms: 60000
          consumer:
            container-factory: prodKafkaListenerContainerFactory
            group-id: prod-group
            auto-offset-reset: latest
          producer:
            kafka-template: prodKafkaTemplate
```

### 示例 3: 多数据源混合配置

部分数据源有认证，部分无认证：

```yaml
zf:
  kafka:
    dynamic:
      primary: local-kafka
      datasource:
        # 本地开发环境 - 无认证
        local-kafka:
          bootstrap-servers: localhost:9092
          consumer:
            container-factory: localKafkaListenerContainerFactory
            group-id: local-group
          producer:
            kafka-template: localKafkaTemplate
            
        # 测试环境 - SASL_PLAINTEXT 认证
        test-kafka:
          bootstrap-servers: kafka-test.example.com:9092
          security:
            protocol: SASL_PLAINTEXT
            mechanism: PLAIN
            username: test-user
            password: test-password
          consumer:
            container-factory: testKafkaListenerContainerFactory
            group-id: test-group
          producer:
            kafka-template: testKafkaTemplate
            
        # 生产环境 - SASL_SSL 认证
        prod-kafka:
          bootstrap-servers: kafka-prod.example.com:9093
          security:
            protocol: SASL_SSL
            mechanism: PLAIN
            username: ${KAFKA_PROD_USERNAME}
            password: ${KAFKA_PROD_PASSWORD}
            trust-store-location: file:./jks/prod_truststore.jks
            trust-store-password: ${PROD_TRUSTSTORE_PASSWORD}
          consumer:
            container-factory: prodKafkaListenerContainerFactory
            group-id: prod-group
          producer:
            kafka-template: prodKafkaTemplate
```

## 使用方法

### 1. 配置文件设置

在 `application-kafka.yaml` 或对应环境的配置文件中添加安全配置。

### 2. 环境变量配置（推荐）

为了安全起见，建议将敏感信息配置为环境变量：

```bash
# Linux/Mac
export KAFKA_USERNAME=your-username
export KAFKA_PASSWORD=your-password
export TRUSTSTORE_PASSWORD=your-truststore-password

# Windows
set KAFKA_USERNAME=your-username
set KAFKA_PASSWORD=your-password
set TRUSTSTORE_PASSWORD=your-truststore-password
```

### 3. 代码中使用

配置完成后，代码中使用方式与之前完全相同，认证过程对应用代码透明：

```java
@Service
public class KafkaMessageService {
    
    @Resource
    private KafkaTemplate<String, String> prodKafkaTemplate;
    
    public void sendMessage(String topic, String message) {
        // 自动使用配置的 SASL 认证
        prodKafkaTemplate.send(topic, message);
    }
}

@Component
public class KafkaMessageListener {
    
    @KafkaListener(topics = "my-topic", 
                   containerFactory = "prodKafkaListenerContainerFactory")
    public void listen(String message) {
        // 自动使用配置的 SASL 认证
        System.out.println("Received: " + message);
    }
}
```

## SSL 信任存储配置

### 生成信任存储文件

如果使用 SASL_SSL，需要准备信任存储文件：

```bash
# 从 Kafka 服务器获取证书
openssl s_client -connect kafka-server:9093 -showcerts < /dev/null 2>/dev/null | \
  openssl x509 -outform PEM > kafka-server.pem

# 导入证书到信任存储
keytool -import -alias kafka-server -file kafka-server.pem \
  -keystore client_truststore.jks -storepass your-password -noprompt
```

### 信任存储路径配置

支持两种路径格式：

1. **类路径（classpath）**：
   ```yaml
   trust-store-location: classpath:jks/client_truststore.jks
   ```
   文件放在 `src/main/resources/jks/` 目录下

2. **文件系统（file）**：
   ```yaml
   trust-store-location: file:./jks/client_truststore.jks
   ```
   文件放在应用运行目录的 `jks/` 子目录下

## 安全最佳实践

### 1. 凭据管理

- ✅ **推荐**: 使用环境变量存储敏感信息
  ```yaml
  username: ${KAFKA_USERNAME}
  password: ${KAFKA_PASSWORD}
  ```

- ✅ **推荐**: 使用加密属性（如果项目支持）
  ```yaml
  password: enc`encrypted-password-here`
  ```

- ❌ **不推荐**: 明文存储密码在配置文件中
  ```yaml
  password: plain-text-password  # 不安全！
  ```

### 2. 协议选择

- ✅ **生产环境**: 使用 `SASL_SSL` 协议
- ⚠️ **内网环境**: 可以使用 `SASL_PLAINTEXT`（如果网络已加密）
- ❌ **不推荐**: 生产环境使用 `PLAINTEXT` 或 `SASL_PLAINTEXT`

### 3. SSL 配置

- ✅ **生产环境**: 启用主机名验证
  ```yaml
  properties:
    ssl.endpoint.identification.algorithm: https
  ```

- ⚠️ **开发环境**: 可以禁用主机名验证（仅用于测试）
  ```yaml
  properties:
    ssl.endpoint.identification.algorithm: ""
  ```

### 4. 密码轮换

定期更换 Kafka 认证密码和 SSL 证书：

1. 在 Kafka 服务器上创建新用户或更新密码
2. 更新应用配置或环境变量
3. 重启应用

### 5. 权限最小化

为每个应用创建独立的 Kafka 用户，并授予最小必要权限：

```bash
# 创建用户（在 Kafka 服务器上）
kafka-configs.sh --zookeeper localhost:2181 --alter \
  --add-config 'SCRAM-SHA-256=[password=app-password]' \
  --entity-type users --entity-name app-user

# 授予特定主题的读写权限
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 \
  --add --allow-principal User:app-user \
  --operation Read --operation Write --topic my-topic
```

## 故障排查

### 1. 认证失败

**错误信息**:
```
org.apache.kafka.common.errors.SaslAuthenticationException: 
Authentication failed: Invalid username or password
```

**解决方法**:
- 检查用户名和密码是否正确
- 确认 Kafka 服务器上用户已创建
- 检查环境变量是否正确设置

### 2. SSL 连接失败

**错误信息**:
```
javax.net.ssl.SSLHandshakeException: 
sun.security.validator.ValidatorException: PKIX path building failed
```

**解决方法**:
- 检查信任存储文件路径是否正确
- 确认信任存储文件包含正确的证书
- 检查信任存储密码是否正确

### 3. 主机名验证失败

**错误信息**:
```
javax.net.ssl.SSLPeerUnverifiedException: 
Host name 'kafka-server' does not match the certificate subject
```

**解决方法**:
- 确保证书中的主机名与配置的 bootstrap-servers 匹配
- 或者禁用主机名验证（仅开发环境）:
  ```yaml
  properties:
    ssl.endpoint.identification.algorithm: ""
  ```

### 4. 配置未生效

**症状**: 配置了安全设置但仍然使用非认证连接

**解决方法**:
- 检查配置文件格式是否正确（YAML 缩进）
- 确认配置文件被正确加载（检查 active profile）
- 查看启动日志，确认安全配置已应用：
  ```
  kafka-multiple-datasource => add a kafka template named [xxx] 
  with security protocol [SASL_SSL] success.
  ```

### 5. 查看详细日志

启用 Kafka 客户端的 DEBUG 日志：

```yaml
logging:
  level:
    org.apache.kafka: DEBUG
    net.zf.framework.kafka: DEBUG
```

## 配置验证

### 启动日志检查

应用启动时，会输出类似以下日志：

```
INFO  - kafka-multiple-datasource => add a kafka template named [prodKafkaTemplate] 
        with security protocol [SASL_SSL] success.
INFO  - kafka-dynamic-datasource => add a kafka listener container factory named 
        [prodKafkaListenerContainerFactory] with security protocol [SASL_SSL] success.
INFO  - kafka-multiple-datasource initial loaded [3] datasource, 
        primary datasource named [prod-kafka]
```

### 连接测试

发送测试消息验证配置：

```java
@SpringBootTest
public class KafkaSecurityTest {
    
    @Resource
    private KafkaTemplate<String, String> prodKafkaTemplate;
    
    @Test
    public void testSaslConnection() {
        String topic = "test-topic";
        String message = "Test message with SASL authentication";
        
        // 发送消息
        prodKafkaTemplate.send(topic, message).addCallback(
            result -> System.out.println("Message sent successfully"),
            ex -> System.err.println("Failed to send message: " + ex.getMessage())
        );
    }
}
```

## 常见问题

### Q1: 是否支持其他 SASL 机制（如 SCRAM-SHA-256）？

A: 当前版本仅支持 PLAIN 机制。未来版本将支持 SCRAM-SHA-256、SCRAM-SHA-512 等机制。

### Q2: 可以为生产者和消费者配置不同的认证吗？

A: 不可以。同一个数据源的生产者和消费者使用相同的安全配置。如需不同认证，请配置多个数据源。

### Q3: 如何在不重启应用的情况下更新密码？

A: 当前版本不支持动态更新密码。密码更新需要重启应用。未来版本将考虑支持凭据动态刷新。

### Q4: 是否支持 Kerberos 认证？

A: 当前版本不支持 Kerberos（GSSAPI）认证。如有需求，请联系开发团队。

### Q5: 配置了安全设置后，现有代码需要修改吗？

A: 不需要。安全认证对应用代码完全透明，现有的生产者和消费者代码无需任何修改。

## 参考资料

- [Apache Kafka Security Documentation](https://kafka.apache.org/documentation/#security)
- [Kafka SASL/PLAIN Configuration](https://kafka.apache.org/documentation/#security_sasl_plain)
- [Spring Kafka Security](https://docs.spring.io/spring-kafka/reference/html/#security)

## 技术支持

如遇到问题，请提供以下信息：

1. 完整的配置文件（脱敏后）
2. 应用启动日志
3. 错误堆栈信息
4. Kafka 服务器版本和配置

联系方式：[技术支持邮箱或内部工单系统]
