# 需求文档

## 简介

本文档规定了增强 `endpoint-spring-boot-starter-kafka` 模块以支持多个 Kafka 数据源的 SASL/PLAIN 认证的需求。该增强功能将允许用户通过配置文件为每个 Kafka 数据源配置认证凭据，支持使用 SASL/PLAIN 机制的 SSL 和非 SSL 连接。

## 术语表

- **SASL**: 简单认证和安全层（Simple Authentication and Security Layer），用于互联网协议中认证和数据安全的框架
- **PLAIN**: 与 SASL 一起使用的简单用户名/密码认证机制
- **SSL**: 安全套接字层（Secure Sockets Layer），用于建立经过认证和加密链接的协议
- **Kafka 数据源**: 配置的 Kafka 集群连接，具有特定的引导服务器、消费者和生产者设置
- **ZfKafkaProperties**: 管理 Kafka 数据源配置的配置属性类
- **KafkaDynamicSourceRegister**: 负责动态注册多个 Kafka 数据源的组件

## 需求

### 需求 1: SASL/PLAIN 认证配置

**用户故事:** 作为开发人员，我希望通过 YAML 配置文件为每个 Kafka 数据源配置 SASL/PLAIN 认证凭据，以便我可以安全地连接到需要认证的 Kafka 集群。

#### 验收标准

1. WHEN 开发人员向 Kafka 数据源添加安全配置时，THE 系统 SHALL 支持用于 SASL/PLAIN 认证的用户名和密码属性
2. WHEN 开发人员指定安全协议时，THE 系统 SHALL 支持 SASL_PLAINTEXT 和 SASL_SSL 协议类型
3. WHEN 开发人员配置 SASL 机制时，THE 系统 SHALL 支持 PLAIN 机制类型
4. WHERE 启用 SSL 时，THE 系统 SHALL 支持 trust-store-location 和 trust-store-password 配置属性
5. WHEN 提供安全属性时，THE 系统 SHALL 将这些属性应用于生产者和消费者配置

### 需求 2: 多数据源认证支持

**用户故事:** 作为开发人员，我希望每个 Kafka 数据源都有独立的认证配置，以便我可以连接到具有不同安全要求的多个 Kafka 集群。

#### 验收标准

1. WHEN 配置多个 Kafka 数据源时，THE 系统 SHALL 允许每个数据源具有独立的安全设置
2. WHEN 数据源未指定安全配置时，THE 系统 SHALL 使用非认证连接模式
3. WHEN 数据源从父配置继承时，THE 系统 SHALL 合并安全属性，子级特定覆盖优先
4. THE 系统 SHALL 验证当安全协议为 SASL_PLAINTEXT 或 SASL_SSL 时所需的安全属性是否存在

### 需求 3: 配置属性增强

**用户故事:** 作为开发人员，我希望为 SASL 认证提供清晰且类型安全的配置属性，以便我可以避免配置错误并了解可用选项。

#### 验收标准

1. THE 系统 SHALL 提供一个 ZfKafkaSecurity 配置类，包含协议、机制、用户名、密码和 SSL 设置的属性
2. THE 系统 SHALL 将 ZfKafkaSecurity 作为嵌套配置属性集成到 ZfKafkaProperties 中
3. THE 系统 SHALL 支持通过 zf.kafka.dynamic.datasource.[name].security 命名空间进行配置
4. THE 系统 SHALL 为可选的安全属性提供默认值
5. THE 系统 SHALL 在应用程序启动时验证安全配置

### 需求 4: 带认证的动态注册

**用户故事:** 作为开发人员，我希望动态源注册器在创建 Kafka 模板和监听器工厂时自动应用认证设置，以便认证对我的应用程序代码透明。

#### 验收标准

1. WHEN KafkaDynamicSourceRegister 创建生产者配置时，THE 系统 SHALL 在配置了安全性时包含 SASL 认证属性
2. WHEN KafkaDynamicSourceRegister 创建消费者配置时，THE 系统 SHALL 在配置了安全性时包含 SASL 认证属性
3. WHEN 启用 SSL 时，THE 系统 SHALL 在生产者和消费者配置中配置 SSL 信任存储属性
4. THE 系统 SHALL 从用户名和密码属性构造正确的 JAAS 配置字符串
5. THE 系统 SHALL 记录安全配置状态而不暴露敏感凭据

### 需求 5: 向后兼容性

**用户故事:** 作为具有现有 Kafka 配置的开发人员，我希望认证增强功能向后兼容，以便我现有的非认证配置可以继续工作而无需修改。

#### 验收标准

1. WHEN 未提供安全配置时，THE 系统 SHALL 像以前一样创建不带认证的 Kafka 连接
2. WHEN 使用现有配置文件时，THE 系统 SHALL 不需要对非认证场景进行任何更改
3. THE 系统 SHALL 维护所有现有的配置属性和行为
4. THE 系统 SHALL 不破坏现有的 KafkaTemplate 和监听器工厂注册

### 需求 6: 配置示例文档

**用户故事:** 作为开发人员，我希望有清晰的 SASL/PLAIN 认证配置示例，以便我可以快速设置经过认证的 Kafka 连接。

#### 验收标准

1. THE 系统 SHALL 提供 SASL_PLAINTEXT 认证的 YAML 配置示例
2. THE 系统 SHALL 提供带有信任存储的 SASL_SSL 认证的 YAML 配置示例
3. THE 系统 SHALL 提供显示具有不同认证设置的多个数据源的示例
4. THE 系统 SHALL 记录所有与安全相关的配置属性及其描述
