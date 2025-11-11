# 设计文档

## 概述

本设计文档概述了为现有的 endpoint-spring-boot-starter-kafka 模块添加 SASL（简单认证和安全层）安全认证的实现方法。该设计扩展了当前的动态多数据源配置架构，同时保持完全的向后兼容性。

该增强功能将引入新的安全配置属性，与现有的 `zf.kafka.dynamic` 配置结构无缝集成，允许开发人员为每个数据源配置 SASL 认证，而不会破坏现有功能。

## 架构设计

### 当前架构分析

现有的 Kafka starter 遵循清洁架构模式：

- **配置层**: `ZfKafkaProperties` 管理 YAML 配置绑定
- **注册层**: `KafkaDynamicSourceRegister` 处理动态 Bean 注册
- **属性层**: `ZfKafkaProducer` 和 `ZfKafkaConsumer` 定义连接属性
- **辅助层**: Kafka 操作的工具类

### 增强架构

SASL 安全增强将通过以下方式扩展此架构：

1. **添加安全属性**: 将新的安全配置类集成到现有属性层次结构中
2. **扩展注册逻辑**: 增强 Bean 注册以包含 SASL 配置
3. **安全验证**: 为安全配置添加新的验证层
4. **向后兼容**: 确保现有配置继续正常工作

## 组件和接口

### 1. 安全配置属性

#### ZfKafkaSecurity 类
```java
@Data
public class ZfKafkaSecurity {
    private String mechanism;           // PLAIN, SCRAM-SHA-256, SCRAM-SHA-512
    private String username;            // 用户名
    private String password;            // 密码
    private SslConfig ssl;              // 可选的 SSL 配置
    private Map<String, String> properties; // 额外的 SASL 属性
}
```

#### SslConfig 类
```java
@Data
public class SslConfig {
    private String protocol;            // SSL, TLS, TLSv1.2 等
    private String truststoreLocation;  // 信任库位置
    private String truststorePassword;  // 信任库密码
    private String keystoreLocation;    // 密钥库位置
    private String keystorePassword;    // 密钥库密码
    private String keyPassword;         // 密钥密码
}
```

### 2. 增强的属性类

#### 更新的 ZfKafkaProperties
- 添加 `@NestedConfigurationProperty private ZfKafkaSecurity security` 字段
- 保持现有结构以确保向后兼容性

#### 更新的 ZfKafkaProducer 和 ZfKafkaConsumer
- 添加 `@NestedConfigurationProperty private ZfKafkaSecurity security` 字段
- 允许每个组件的安全配置覆盖

### 3. 安全配置构建器

#### SecurityConfigBuilder 类
```java
@Component
public class SecurityConfigBuilder {
    public Map<String, Object> buildSecurityConfig(ZfKafkaSecurity security);
    public void validateSecurityConfig(ZfKafkaSecurity security);
    public Map<String, Object> buildSslConfig(SslConfig ssl);
}
```

### 4. 增强的注册逻辑

#### 更新的 KafkaDynamicSourceRegister
- 将安全配置集成到生产者和消费者配置方法中
- 在 Bean 注册期间添加安全验证
- 保持现有注册流程并增加安全增强功能

## 数据模型

### 配置结构

增强的配置将支持以下 YAML 结构：

```yaml
zf:
  kafka:
    dynamic:
      primary: secure-kafka
      # Global security settings (optional)
      security:
        mechanism: SCRAM-SHA-256
        username: ${KAFKA_USERNAME}
        password: ${KAFKA_PASSWORD}
        ssl:
          protocol: TLS
          truststoreLocation: /path/to/truststore.jks
          truststorePassword: ${TRUSTSTORE_PASSWORD}
      
      datasource:
        secure-kafka:
          bootstrap-servers: secure-broker:9093
          security:
            mechanism: SCRAM-SHA-256
            username: secure-user
            password: ${SECURE_PASSWORD}
            ssl:
              protocol: TLS
              truststoreLocation: /path/to/secure-truststore.jks
              truststorePassword: ${SECURE_TRUSTSTORE_PASSWORD}
          producer:
            kafka-template: secureKafkaTemplate
          consumer:
            container-factory: secureKafkaListenerContainerFactory
            group-id: secure-group
        
        legacy-kafka:
          bootstrap-servers: legacy-broker:9092
          # 无安全配置 - 保持向后兼容性
          producer:
            kafka-template: legacyKafkaTemplate
          consumer:
            container-factory: legacyKafkaListenerContainerFactory
```

### 安全机制映射

| 机制 | 必需属性 | 可选属性 |
|-----------|-------------------|-------------------|
| PLAIN | username, password | - |
| SCRAM-SHA-256 | username, password | - |
| SCRAM-SHA-512 | username, password | - |

## 错误处理

### 验证策略

1. **启动验证**: 在应用程序启动期间验证所有安全配置
2. **快速失败方法**: 如果关键安全配置无效，则停止应用程序启动
3. **清晰的错误消息**: 提供具体的错误消息和配置示例

### 错误场景

1. **缺少必需属性**: 清楚地指示每种机制需要哪些属性
2. **无效机制**: 在错误消息中列出支持的机制
3. **SSL 配置问题**: 具体的 SSL 验证错误和故障排除提示
4. **凭据访问问题**: 验证凭据可访问性而不暴露值

### 安全日志

1. **凭据掩码**: 日志中所有敏感信息都被掩码
2. **安全事件**: 记录认证成功/失败事件而不暴露凭据
3. **配置验证**: 记录安全配置验证结果

## 测试策略

### 单元测试

1. **配置绑定测试**: 验证 YAML 配置正确绑定到安全属性
2. **安全构建器测试**: 测试不同机制的安全配置构建
3. **验证测试**: 测试安全配置验证逻辑
4. **向后兼容性测试**: 确保现有配置继续工作

### 集成测试

1. **多数据源安全测试**: 测试具有不同安全配置的多个数据源
2. **SSL + SASL 集成测试**: 测试 SSL 和 SASL 认证的组合
3. **认证失败测试**: 测试认证失败的正确处理
4. **配置覆盖测试**: 测试安全配置继承和覆盖

### 安全测试

1. **凭据掩码测试**: 验证敏感信息在日志中被正确掩码
2. **配置验证测试**: 测试安全配置验证边缘情况
3. **SSL 证书测试**: 测试 SSL 证书验证和错误处理

## 实施阶段

### 阶段 1: 核心安全属性
- 实现 `ZfKafkaSecurity` 和 `SslConfig` 类
- 使用安全字段扩展现有属性类
- 更新配置绑定

### 阶段 2: 安全配置构建器
- 实现带有验证逻辑的 `SecurityConfigBuilder`
- 添加对 PLAIN、SCRAM-SHA-256 和 SCRAM-SHA-512 机制的支持
- 实现 SSL 配置构建

### 阶段 3: 注册增强
- 更新 `KafkaDynamicSourceRegister` 以集成安全配置
- 增强生产者和消费者配置方法
- 向注册过程添加安全验证

### 阶段 4: 错误处理和日志记录
- 实现全面的错误处理和验证
- 添加带有凭据掩码的安全感知日志记录
- 创建带有配置示例的详细错误消息

### 阶段 5: 测试和文档
- 实现全面的测试套件
- 使用安全配置示例更新 README
- 添加常见安全问题的故障排除指南

## 安全考虑

1. **凭据保护**: 所有凭据在日志和错误消息中被掩码
2. **配置加密**: 支持 Spring Boot 加密属性
3. **验证安全**: 验证配置而不暴露敏感数据
4. **SSL 集成**: 正确的 SSL 证书验证和错误处理
5. **认证上下文隔离**: 每个数据源的独立认证上下文