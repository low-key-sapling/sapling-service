# Sapling Service - 企业级微服务脚手架

<div align="center">

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)
![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.5-orange.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)

基于 Spring Boot 3.2.5 + DDD 架构的企业级微服务开发脚手架

[快速开始](#快速开始) • [技术架构](#技术架构) • [项目结构](#项目结构) • [核心组件](#核心组件) • [开发指南](#开发指南)

</div>

---

## 📖 项目简介

Sapling Service 是一个基于 Spring Boot 3.2.5 的企业级微服务开发脚手架，采用 DDD（领域驱动设计）分层架构，集成了常用的技术组件和最佳实践，旨在帮助开发团队快速构建高质量、可维护的微服务应用。

### ✨ 核心特性

- 🏗️ **DDD 分层架构**：清晰的领域驱动设计分层，职责明确
- 🔧 **开箱即用**：集成常用技术栈，快速启动项目开发
- 🚀 **高性能**：基于 Spring Boot 3.2.5 + Java 17，性能优化
- 🔐 **安全可靠**：内置加密、安全组件，保障系统安全
- 📊 **多数据源支持**：支持 MySQL、PostgreSQL、达梦、人大金仓等多种数据库
- 🔍 **全文检索**：集成 Elasticsearch（BBoss），支持复杂查询
- 📨 **消息队列**：集成 Kafka，支持 SASL 认证和多数据源
- 🎯 **规则引擎**：集成 LiteFlow，支持灵活的业务规则配置
- 📈 **链路追踪**：集成 TLog，支持分布式链路追踪
- 🛠️ **代码生成**：内置 MyBatis Plus 代码生成器，提高开发效率

---

## 🎯 技术架构

### 核心技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.2.5 | 核心框架 |
| **Spring Framework** | 6.x | 由 Spring Boot 3.x 自动管理 |
| **JDK** | 17 | Java 开发工具包（最低要求） |
| **Maven** | 3.6+ | 项目构建工具 |

### 数据持久层

| 技术 | 版本 | 说明 |
|------|------|------|
| **MyBatis Plus** | 3.5.5 | ORM 框架 |
| **MySQL** | 8.0.33 | 关系型数据库 |
| **PostgreSQL** | 支持 | 关系型数据库 |
| **达梦数据库** | 7.1.4.74 | 国产数据库 |
| **人大金仓** | 8.6.0 | 国产数据库 |
| **神通数据库** | 支持 | 国产数据库 |
| **瀚高数据库** | 6.0.3 | 国产数据库 |

### 缓存与消息

| 技术 | 版本 | 说明 |
|------|------|------|
| **Redis** | - | 分布式缓存 |
| **Redisson** | 3.27.2 | Redis 客户端 |
| **Kafka** | 3.6.1 | 消息队列 |

### 搜索引擎

| 技术 | 版本 | 说明 |
|------|------|------|
| **Elasticsearch** | 支持 5.x ~ 8.x | 全文检索引擎 |
| **BBoss Elasticsearch** | 6.8.6 | ES 客户端框架，支持注解驱动 |

### 工具库

| 技术 | 版本 | 说明 |
|------|------|------|
| **Lombok** | 1.18.30 | 简化 Java 代码 |
| **MapStruct** | 1.5.5.Final | 对象映射工具 |
| **Hutool** | 5.8.40 | Java 工具类库 |
| **Guava** | 33.4.5-jre | Google 工具库 |
| **Fastjson** | 1.2.83 | JSON 处理 |
| **Jackson** | 2.15.4 | JSON 处理 |
| **Commons Lang3** | 3.19.0 | Apache 通用工具 |
| **Commons Collections4** | 4.4 | Apache 集合工具 |
| **Commons IO** | 2.11.0 | Apache IO 工具 |

### 业务组件

| 技术 | 版本 | 说明 |
|------|------|------|
| **LiteFlow** | 2.15.0 | 轻量级规则引擎，支持脚本 |
| **TLog** | 1.5.2 | 分布式链路追踪 |
| **COLA** | 5.0.0 | 阿里 COLA 架构组件 |
| **Resilience4j** | 2.2.0 | 弹性容错框架 |
| **Forest** | 1.6.4 | 声明式 HTTP 客户端 |
| **Ko-Time** | 2.5.0 | 性能分析工具 |
| **Transmittable ThreadLocal** | 2.12.3 | 线程间值传递 |

### 安全组件

| 技术 | 版本 | 说明 |
|------|------|------|
| **Jasypt** | 1.9.3 | 配置加密 |
| **BouncyCastle** | 1.80 | 加密算法库（bcprov-jdk18on） |

### 日志组件

| 技术 | 版本 | 说明 |
|------|------|------|
| **Logback** | 1.4.14 | 日志框架 |
| **SLF4J** | 1.7.36 | 日志门面 |

---

## 📁 项目结构

```
sapling-service/
├── sapling-dependencies/                          # 依赖管理模块
│   └── pom.xml                                   # 统一管理所有依赖版本
│
├── sapling-framework/                            # 框架组件模块
│   ├── sapling-framework-boot-common/            # 通用工具类
│   ├── sapling-framework-boot-core/              # 核心框架
│   ├── sapling-spring-boot-starter-web/          # Web 组件
│   ├── sapling-spring-boot-starter-kafka/        # Kafka 组件
│   ├── sapling-mybatis-plus-boot-starter/        # MyBatis Plus 组件
│   │   ├── doc/                                  # 文档
│   │   │   └── Mybatis场景启动器快速开发指南.md
│   │   └── README.md                             # 详细使用文档
│   └── sapling-elasticsearch-spring-boot-starter/# Elasticsearch 组件
│       └── README.md                             # 详细使用文档
│
├── sapling-module-system/                        # 业务模块（DDD 分层）
│   ├── sapling-module-system-adapter/            # 适配层（Controller）
│   ├── sapling-module-system-app/                # 应用层（Service）
│   ├── sapling-module-system-client/             # 客户端层（DTO/API）
│   ├── sapling-module-system-domain/             # 领域层（Entity/Repository）
│   └── sapling-module-system-infrastructure/     # 基础设施层（Mapper/Config）
│
├── sapling-server/                               # 服务启动模块
│   ├── src/main/java/                           # 启动类
│   ├── src/main/resources/                      # 配置文件
│   │   ├── application.yaml                     # 主配置
│   │   ├── application-mybatisplus.yaml         # MyBatis Plus 配置
│   │   ├── application-kafka.yaml               # Kafka 配置
│   │   ├── application-elasticsearch.yaml       # Elasticsearch 配置
│   │   ├── application-redis.yaml               # Redis 配置
│   │   ├── application-liteflow.yaml            # LiteFlow 配置
│   │   ├── application-security.yaml            # 安全配置
│   │   ├── logback-spring.xml                   # 日志配置
│   │   └── prod/                                # 生产环境配置
│   ├── src/main/script/                         # 启动脚本
│   │   ├── sapling_service.sh                   # Linux 启动脚本
│   │   └── jvm.options                          # JVM 参数配置
│   └── pom.xml
│
├── docs/                                         # 项目文档
│   ├── kafka-sasl-authentication.md             # Kafka SASL 认证文档
│   ├── kafka-mbws-listeners.md                  # Kafka 多数据源文档
│   └── es-query-wj-child-docs-integration.md    # ES 查询文档
│
├── logs/                                         # 日志目录
├── pom.xml                                       # 父 POM
├── lombok.config                                 # Lombok 配置
└── README.md                                     # 项目说明
```

### 模块说明

#### 1. sapling-dependencies（依赖管理）
统一管理项目所有依赖的版本号，确保版本一致性，避免依赖冲突。

#### 2. sapling-framework（框架组件）
封装常用技术组件，提供开箱即用的功能：

- **sapling-framework-boot-common**：通用工具类、常量、枚举、工具方法等
- **sapling-framework-boot-core**：核心框架功能、AOP、异常处理、基础配置等
- **sapling-spring-boot-starter-web**：Web 相关功能、全局异常处理、API 版本管理、弹性容错等
- **sapling-spring-boot-starter-kafka**：Kafka 消息队列封装，支持多数据源和 SASL 认证
- **sapling-mybatis-plus-boot-starter**：MyBatis Plus 增强、多数据库适配、代码生成器
- **sapling-elasticsearch-spring-boot-starter**：Elasticsearch 搜索引擎封装，基于 BBoss，支持注解驱动

#### 3. sapling-module-system（业务模块）
采用 DDD 分层架构，清晰的职责划分：

```
┌─────────────────────────────────────────┐
│         Adapter Layer (适配层)           │  ← Controller、MQ Consumer
├─────────────────────────────────────────┤
│         Application Layer (应用层)       │  ← Service、流程编排
├─────────────────────────────────────────┤
│         Domain Layer (领域层)            │  ← Entity、Repository、领域服务
├─────────────────────────────────────────┤
│         Infrastructure Layer (基础设施层) │  ← Mapper、Config、外部服务
└─────────────────────────────────────────┘
         ↑                    ↑
         │                    │
    Client Layer          Server Layer
    (客户端层)             (服务启动层)
```

**各层职责：**

- **Client（客户端层）**：对外暴露的 API 接口、DTO 定义
- **Adapter（适配层）**：接收外部请求，调用应用层服务
- **Application（应用层）**：业务流程编排，事务控制
- **Domain（领域层）**：核心业务逻辑，领域模型
- **Infrastructure（基础设施层）**：数据访问、外部服务调用

#### 4. sapling-server（服务启动）
应用程序启动入口，整合所有业务模块。

---

## 🚀 快速开始

### 环境要求

- **JDK 17+**（必须，Spring Boot 3.x 最低要求）
- **Maven 3.6+**
- **MySQL 8.0+ / PostgreSQL 9.6+ / 国产数据库**
- **Redis 5.0+**（可选）
- **Kafka 3.6+**（可选）
- **Elasticsearch 5.x ~ 8.x**（可选）

> **重要提示**: 
> - Spring Boot 3.x 要求最低 Java 17 版本
> - 项目使用 Jakarta EE 9+（javax.* 已迁移至 jakarta.*）

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/your-org/sapling-service.git
cd sapling-service
```

#### 2. 配置数据库

修改 `sapling-server/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/sapling?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

#### 3. 配置 Redis（可选）

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0
```

#### 4. 编译项目

```bash
mvn clean install -DskipTests
```

#### 5. 启动服务

```bash
cd sapling-server
mvn spring-boot:run
```

或者运行启动类：
```java
com.sapling.server.EndpointServerApplication
```

#### 6. 访问应用

默认端口：`29002`

访问地址：`http://localhost:29002/sapling`

健康检查：`http://localhost:29002/sapling/actuator/health`（如已配置）

---

## 🔧 核心组件

### 1. Web 组件（sapling-spring-boot-starter-web）

**功能特性：**
- 全局异常处理
- 统一响应格式
- API 版本管理
- 请求/响应加解密
- 参数校验增强
- 弹性容错（Resilience4j）

**使用示例：**

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping("/{id}")
    public Result<UserDTO> getUser(@PathVariable Long id) {
        // 自动包装响应格式
        return Result.success(userService.getById(id));
    }
}
```

### 2. MyBatis Plus 组件（sapling-mybatis-plus-boot-starter）

**功能特性：**
- 增强型 Mapper（BaseMapperX）
- 增强型条件构造器（智能判空）
- 多数据库自动适配（支持国产数据库）
- 自动填充（创建时间、更新时间等）
- 逻辑删除
- 分页插件
- 代码生成器（配合 MybatisX 插件）
- 性能分析

**使用示例：**

```java
@Mapper
public interface UserMapper extends BaseMapperX<User> {
    // 继承 BaseMapperX，获得增强功能
}

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    public PageResult<User> pageQuery(UserQueryDTO dto, PageParam pageParam) {
        return baseMapper.selectPage(pageParam, new LambdaQueryWrapperX<User>()
            .likeIfPresent(User::getUsername, dto.getUsername())  // 智能判空
            .eqIfPresent(User::getStatus, dto.getStatus())
            .orderByDesc(User::getCreateTime));
    }
}
```

**详细文档：**

- [MyBatis Plus 组件完整文档](sapling-framework/sapling-mybatis-plus-boot-starter/README.md)
- [快速开发指南](sapling-framework/sapling-mybatis-plus-boot-starter/doc/Mybatis场景启动器快速开发指南.md)

### 3. Kafka 组件（sapling-spring-boot-starter-kafka）

**功能特性：**
- 支持原生 Spring Kafka 配置
- 支持多 Kafka 数据源配置
- 支持 SASL/PLAIN 认证
- 支持 SSL 加密连接
- 消息发送封装（KafkaTemplateHelper）
- 消费者自动配置
- 异常处理

**使用示例：**

```java
// 单数据源 - 发送消息
@Resource
private KafkaTemplate<String, String> kafkaTemplate;

public void sendMessage(String topic, String message) {
    kafkaTemplate.send(topic, message);
}

// 单数据源 - 消费消息
@KafkaListener(topics = "user-topic", groupId = "user-group")
public void consume(String message) {
    log.info("Received message: {}", message);
}

// 多数据源 - 发送消息
@Resource(name = "mwsKafkaTemplate")
private KafkaTemplate mwsKafkaTemplate;

public void sendToMws(String topic, String message) {
    mwsKafkaTemplate.send(topic, message);
}

// 多数据源 - 消费消息
@KafkaListener(topics = "topic-artisan", containerFactory = "mwsKafkaListenerContainerFactory")
public void consumeFromMws(String message) {
    log.info("MBWS KAFKA 接收到消息: {}", message);
}
```

**详细文档：**

- [Kafka 组件使用文档](sapling-framework/sapling-spring-boot-starter-kafka/README.md)
- [Kafka SASL 认证配置指南](docs/kafka-sasl-authentication.md)
- [Kafka 多数据源配置示例](docs/kafka-mbws-listeners.md)

### 4. Elasticsearch 组件（sapling-elasticsearch-spring-boot-starter）

**功能特性：**
- 基于 BBoss Elasticsearch 封装
- 注解驱动的索引映射（@ESDsl、@ESMapping）
- 自动创建索引
- 简化的 CRUD 操作
- DSL 查询支持（XML 配置）
- 支持 ES 5.x ~ 8.x 版本
- 多种数据类型支持（text、keyword、date、nested 等）

**使用示例：**

```java
@Data
@ESDsl(value = "esmapper/user.xml", indexName = "user_index")
public class User {
    @ESMapping(ESMappingType.keyword)
    private String id;
    
    @ESMapping(value = ESMappingType.text, analyzer = "ik_max_word", boost = 2)
    private String name;
    
    @ESMapping(ESMappingType._integer)
    private Integer age;
    
    @ESMapping(ESMappingType.date)
    private Date createTime;
}

@Service
public class UserElasticService extends ElasticBaseService<User> {
    
    // 创建索引
    public void init() {
        createIndex();
    }
    
    // 添加文档
    public void addUser(User user) {
        addDocument(user, true);
    }
    
    // 批量添加
    public void batchAdd(List<User> users) {
        addDocuments(users, true);
    }
    
    // 查询文档
    public User getUser(String id) {
        return getDocument(id, User.class);
    }
}
```

**详细文档：**

- [Elasticsearch 组件完整文档](sapling-framework/sapling-elasticsearch-spring-boot-starter/README.md)
- [ES 查询集成文档](docs/es-query-wj-child-docs-integration.md)

### 5. 规则引擎（LiteFlow）

**功能特性：**
- 可视化规则配置
- 动态规则加载
- 支持脚本语言（QLExpress、Groovy、JavaScript）
- 规则链编排
- 支持条件判断、循环、并行执行

**使用示例：**

```java
@Component("userValidateNode")
public class UserValidateNode extends NodeComponent {
    @Override
    public void process() {
        User user = this.getRequestData();
        // 验证逻辑
        if (!isValid(user)) {
            throw new BusinessException("用户验证失败");
        }
    }
}

// 规则链配置（XML 或代码方式）
// THEN(a, b, c) 串行执行
// WHEN(a, b, c) 并行执行
// IF(x, a, b) 条件判断
```

**配置：**

```yaml
liteflow:
  rule-source: config/flow.el.xml  # 规则配置文件
  enable: true
```

### 6. 链路追踪（TLog）

**功能特性：**
- 自动生成 TraceId
- 日志自动打印 TraceId
- 支持跨线程传递
- 支持 RPC 调用传递
- 支持 MDC 集成

**配置：**

```yaml
tlog:
  enable: true
  pattern: '<$traceId>'
```

**日志输出示例：**

```
2024-11-14 10:30:15.123 <1234567890abcdef> [main] INFO  c.s.service.UserService - 用户登录成功
```

---

## 🎨 开发指南

### DDD 分层开发规范

#### 1. Client 层（客户端层）

定义对外暴露的 API 接口和 DTO：

```java
// DTO 定义
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
}

// API 接口定义
public interface UserApi {
    Result<UserDTO> getUser(Long id);
}
```

#### 2. Adapter 层（适配层）

实现 Controller，处理 HTTP 请求：

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController implements UserApi {
    
    @Autowired
    private UserAppService userAppService;
    
    @Override
    @GetMapping("/{id}")
    public Result<UserDTO> getUser(@PathVariable Long id) {
        return Result.success(userAppService.getUser(id));
    }
}
```

#### 3. Application 层（应用层）

编排业务流程，处理事务：

```java
@Service
public class UserAppService {
    
    @Autowired
    private UserDomainService userDomainService;
    
    @Transactional
    public UserDTO getUser(Long id) {
        User user = userDomainService.findById(id);
        return UserConverter.INSTANCE.toDTO(user);
    }
}
```

#### 4. Domain 层（领域层）

核心业务逻辑和领域模型：

```java
// 领域实体
@Data
@TableName("t_user")
public class User {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createTime;
}

// 领域服务
@Service
public class UserDomainService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.selectById(id);
    }
}
```

#### 5. Infrastructure 层（基础设施层）

数据访问和外部服务调用：

```java
@Mapper
public interface UserRepository extends BaseMapper<User> {
    // 数据访问方法
}

@Configuration
public class RedisConfig {
    // 基础设施配置
}
```

### 代码规范

#### 命名规范

- **类名**：大驼峰命名（PascalCase）
- **方法名**：小驼峰命名（camelCase）
- **常量**：全大写，下划线分隔（UPPER_SNAKE_CASE）
- **包名**：全小写，点分隔

#### 注释规范

```java
/**
 * 用户服务类
 * 
 * @author your-name
 * @since 1.0.0
 */
@Service
public class UserService {
    
    /**
     * 根据 ID 获取用户
     * 
     * @param id 用户 ID
     * @return 用户信息
     */
    public User getById(Long id) {
        // 实现逻辑
    }
}
```

#### 异常处理

```java
// 业务异常
public class BusinessException extends RuntimeException {
    private String code;
    private String message;
}

// 全局异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
}
```

### 配置管理

#### 多环境配置

```
resources/
├── application.yml              # 主配置
├── application-dev.yml          # 开发环境
├── application-test.yml         # 测试环境
└── application-prod.yml         # 生产环境
```

#### 配置加密

使用 Jasypt 加密敏感配置：

```yaml
spring:
  datasource:
    password: ENC(加密后的密码)
```

加密命令：
```bash
java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI \
  input="your_password" password="encryption_key" algorithm=PBEWithMD5AndDES
```

---

## 📊 数据库支持

### 支持的数据库

| 数据库 | 驱动版本 | 说明 |
|--------|---------|------|
| MySQL | 8.0.28 | 主流关系型数据库 |
| PostgreSQL | 支持 | 开源关系型数据库 |
| 达梦数据库 | 7.1.4.74 | 国产数据库 |
| 人大金仓 | 8.6.0 | 国产数据库 |
| 神通数据库 | 支持 | 国产数据库 |
| 瀚高数据库 | 6.0.3 | 国产数据库 |

### 切换数据库

修改 `application.yml` 中的数据库配置：

```yaml
# MySQL
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/sapling

# PostgreSQL
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/sapling

# 达梦数据库
spring:
  datasource:
    driver-class-name: dm.jdbc.driver.DmDriver
    url: jdbc:dm://localhost:5236/sapling

# 人大金仓
spring:
  datasource:
    driver-class-name: com.kingbase8.Driver
    url: jdbc:kingbase8://localhost:54321/sapling
```

---

## 🔒 安全特性

### 1. 配置加密

使用 Jasypt 加密敏感配置信息。

### 2. 数据加密

使用 BouncyCastle 提供的加密算法。

### 3. 接口加解密

支持请求/响应数据的自动加解密。

### 4. SQL 注入防护

MyBatis Plus 自动防护 SQL 注入。

---

## 📈 性能优化

### 1. 数据库优化

- 使用 MyBatis Plus 分页插件
- 合理使用索引
- 避免 N+1 查询问题

### 2. 缓存优化

- Redis 缓存热点数据
- 本地缓存 + 分布式缓存
- 缓存预热和更新策略

### 3. 异步处理

- 使用 Kafka 异步处理耗时操作
- Spring @Async 异步方法
- 线程池配置优化

### 4. 性能监控

- Ko-Time 性能分析
- TLog 链路追踪
- 日志分析

---

## 📝 日志管理

### 日志配置

使用 Logback 作为日志框架：

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

### 日志级别

- **ERROR**：错误信息
- **WARN**：警告信息
- **INFO**：一般信息
- **DEBUG**：调试信息
- **TRACE**：跟踪信息

---

## 🧪 测试

### 单元测试

```java
@SpringBootTest
public class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testGetUser() {
        User user = userService.getById(1L);
        assertNotNull(user);
    }
}
```

### 集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
```

---

## 📦 部署

### 打包

```bash
mvn clean package -DskipTests
```

生成的文件：
```
sapling-server/target/
├── sapling_service.jar          # 可执行 JAR
├── lib/                         # 依赖库
├── config/                      # 配置文件
│   ├── application.yaml
│   ├── application-*.yaml
│   └── flow/                    # LiteFlow 规则文件
├── jks/                         # 证书文件（SSL/TLS）
└── bin/                         # 启动脚本
    ├── sapling_service.sh       # Linux 启动脚本
    ├── sapling_service-watch.sh # 监控脚本
    └── jvm.options              # JVM 参数配置
```

### 运行

**方式一：直接运行 JAR**

```bash
java -jar sapling-server/target/sapling_service.jar
```

**方式二：使用启动脚本（推荐）**

```bash
cd sapling-server/target/bin
chmod +x sapling_service.sh
./sapling_service.sh start
```

**脚本命令：**
- `start`：启动服务
- `stop`：停止服务
- `restart`：重启服务
- `status`：查看状态

**自定义 JVM 参数：**

编辑 `bin/jvm.options` 文件：

```
-Xms2g
-Xmx2g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-Dspring.profiles.active=prod
```

### Docker 部署

**Dockerfile 示例：**

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 复制构建产物
COPY sapling-server/target/sapling_service.jar app.jar
COPY sapling-server/target/config config/
COPY sapling-server/target/lib lib/

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 暴露端口
EXPOSE 29002

# JVM 参数
ENV JAVA_OPTS="-Xms1g -Xmx1g -XX:+UseG1GC"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**构建镜像：**

```bash
docker build -t sapling-service:1.0.0 .
```

**运行容器：**

```bash
docker run -d \
  -p 29002:29002 \
  -v /var/log/sapling:/var/log/sapling \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xms2g -Xmx2g" \
  --name sapling-service \
  sapling-service:1.0.0
```

**Docker Compose 示例：**

```yaml
version: '3.8'

services:
  sapling-service:
    image: sapling-service:1.0.0
    container_name: sapling-service
    ports:
      - "29002:29002"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JAVA_OPTS=-Xms2g -Xmx2g -XX:+UseG1GC
    volumes:
      - /var/log/sapling:/var/log/sapling
      - ./config:/app/config
    restart: unless-stopped
    networks:
      - sapling-network

  mysql:
    image: mysql:8.0
    container_name: sapling-mysql
    environment:
      - MYSQL_ROOT_PASSWORD=your_password
      - MYSQL_DATABASE=sapling
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - sapling-network

  redis:
    image: redis:7-alpine
    container_name: sapling-redis
    ports:
      - "6379:6379"
    networks:
      - sapling-network

networks:
  sapling-network:
    driver: bridge

volumes:
  mysql-data:
```

**启动：**

```bash
docker-compose up -d
```

---

## 🤝 贡献指南

欢迎贡献代码、提出问题和建议！

### 贡献流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 代码规范

- 遵循 Java 编码规范
- 使用 Lombok 简化代码
- 添加必要的注释和文档
- 编写单元测试
- 确保代码通过 CI 检查

### 代码审查

所有 Pull Request 都需要经过代码审查才能合并。

---

## 📚 相关文档

### 框架组件文档

- [MyBatis Plus 组件使用文档](sapling-framework/sapling-mybatis-plus-boot-starter/README.md)
- [Kafka 组件使用文档](sapling-framework/sapling-spring-boot-starter-kafka/README.md)
- [Elasticsearch 组件使用文档](sapling-framework/sapling-elasticsearch-spring-boot-starter/README.md)

### 技术文档

- [Kafka SASL 认证配置指南](docs/kafka-sasl-authentication.md)
- [Kafka 多数据源配置示例](docs/kafka-mbws-listeners.md)
- [Elasticsearch 查询集成文档](docs/es-query-wj-child-docs-integration.md)
- [MyBatis Plus 快速开发指南](sapling-framework/sapling-mybatis-plus-boot-starter/doc/Mybatis场景启动器快速开发指南.md)

### 外部文档

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis Plus 官方文档](https://baomidou.com/)
- [LiteFlow 官方文档](https://liteflow.cc/)
- [BBoss Elasticsearch 文档](https://esdoc.bbossgroups.com/)
- [TLog 官方文档](https://tlog.yomahub.com/)

---

## 📄 许可证

本项目采用 Apache License 2.0 许可证。详见 [LICENSE](LICENSE) 文件。

---

## 📞 联系方式

- **项目地址**：https://github.com/your-org/sapling-service
- **问题反馈**：https://github.com/your-org/sapling-service/issues
- **邮箱**：support@example.com

---

## 🙏 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot) - 核心框架
- [MyBatis Plus](https://baomidou.com/) - ORM 框架
- [LiteFlow](https://liteflow.cc/) - 规则引擎
- [TLog](https://tlog.yomahub.com/) - 链路追踪
- [Hutool](https://hutool.cn/) - Java 工具库
- [BBoss Elasticsearch](https://esdoc.bbossgroups.com/) - ES 客户端
- [Redisson](https://redisson.org/) - Redis 客户端
- [Apache Kafka](https://kafka.apache.org/) - 消息队列
- [Resilience4j](https://resilience4j.readme.io/) - 弹性容错
- [Forest](https://forest.dtflyx.com/) - HTTP 客户端
- [MapStruct](https://mapstruct.org/) - 对象映射
- [Lombok](https://projectlombok.org/) - 代码简化
- [Guava](https://github.com/google/guava) - Google 工具库

---

## 📋 更新日志

### v1.0.0 (2024-11-14)

**核心功能：**
- ✅ 基于 Spring Boot 3.2.5 + Java 17
- ✅ DDD 分层架构设计
- ✅ MyBatis Plus 增强组件（支持多数据库）
- ✅ Kafka 多数据源支持（支持 SASL 认证）
- ✅ Elasticsearch 注解驱动（基于 BBoss）
- ✅ LiteFlow 规则引擎集成
- ✅ TLog 链路追踪集成
- ✅ Resilience4j 弹性容错
- ✅ Redis 分布式缓存（Redisson）
- ✅ 配置加密（Jasypt）
- ✅ 完善的文档和示例

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star ⭐**

Made with ❤️ by Sapling Team

</div>
