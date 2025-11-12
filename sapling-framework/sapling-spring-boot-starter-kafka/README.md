# Introduce

[润物无声]

- 基于spring-kafka的轻量级封装
- 只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑。

[核心功能]

- 支持原生配置
- 支持多Kafka配置
- 支持 SASL/PLAIN 认证（新增）
- 支持 SSL 加密连接（新增）

# Getting Started

##  Step1 POM依赖引入 （必选）

```xml
    <dependency>
        <groupId>com.sapling</groupId>
        <artifactId>sapling-kafka-spring-boot-starter</artifactId>
        <version>${xxx}</version>  <!-- 目前已发布 1.0.0 -->
    </dependency>
```


## Step2  Configuration 

我们这里以yml为例  


### 原生支持: 支持spring-kafka原生配置

包含但不局限于以下属性

```yaml
spring:
  kafka:
    bootstrap-servers: 127.0.0.1:9092
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```


### 多Kafka数据源配置

遵循就近原则, 配置粒度越小, 优先级越高

```yaml
zf:
  kafka:
    dynamic:
      # Primary data source is not allowed to be empty.
      primary: mws-kafka
      producer:
        key-serializer: org.apache.kafka.common.serialization.StringSerializer
        value-serializer: org.apache.kafka.common.serialization.StringSerializer
      consumer:
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      datasource:
        mws-kafka:
          bootstrap-servers: 10.11.110.157:9888
          consumer:
            # Consumer concurrent kafka listener container factory is not allowed to be empty.
            container-factory: mwsKafkaListenerContainerFactory
            auto-offset-reset: none
          producer:
            # Producer kafka template bean name is not allowed to be empty.
            kafka-template: mwsKafkaTemplate

        sxm-kafka:
          bootstrap-servers: 10.11.110.159:9888
          consumer:
            container-factory: sxmKafkaListenerContainerFactory
            auto-offset-reset: earliest
          producer:
            # Producer kafka template bean name is not allowed to be empty.
            kafka-template: sxmKafkaTemplate
```

### SASL/PLAIN 认证配置（新增）

支持为每个 Kafka 数据源配置独立的 SASL/PLAIN 认证：

```yaml
zf:
  kafka:
    dynamic:
      primary: secure-kafka
      datasource:
        # SASL_PLAINTEXT 认证示例（无 SSL）
        secure-kafka:
          bootstrap-servers: kafka-server.example.com:9092
          security:
            protocol: SASL_PLAINTEXT  # 安全协议
            mechanism: PLAIN           # SASL 机制
            username: kafka-user       # 用户名
            password: kafka-password   # 密码
          consumer:
            container-factory: secureKafkaListenerContainerFactory
            group-id: secure-group
          producer:
            kafka-template: secureKafkaTemplate
            
        # SASL_SSL 认证示例（带 SSL 加密）
        prod-kafka:
          bootstrap-servers: kafka-prod.example.com:9093
          security:
            protocol: SASL_SSL                                    # SASL + SSL
            mechanism: PLAIN
            username: ${KAFKA_USERNAME}                           # 从环境变量读取
            password: ${KAFKA_PASSWORD}
            trust-store-location: file:./jks/client_truststore.jks
            trust-store-password: ${TRUSTSTORE_PASSWORD}
            properties:
              ssl.endpoint.identification.algorithm: ""
              request.timeout.ms: 60000
          consumer:
            container-factory: prodKafkaListenerContainerFactory
            group-id: prod-group
          producer:
            kafka-template: prodKafkaTemplate
```

**详细配置说明请参考**: [Kafka SASL/PLAIN 认证配置指南](../../docs/kafka-sasl-authentication.md)
```

# Usage

##  原生使用

KafkaController 
```java
@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final static String MSG_WITH_HELPER = "message from kafka starter with kafkaTemplateHelper";
    private final static String MSG = "message from kafka starter with original kafkaTemplate ";
    private final static String RSP = "SUCCESS";
    private final static String TOPIC = "topic-artisan";
    @Resource
    private KafkaTemplate kafkaTemplate;

    @Resource
    private KafkaTemplateHelper kafkaTemplateHelper;


    @GetMapping("/send")
    public String send() {
        kafkaTemplate.send(TOPIC, MSG);
        return RSP;
    }

    @GetMapping("/send2")
    public String sendBykafkaTemplateHelper() {
        kafkaTemplateHelper.syncSend(kafkaTemplate, TOPIC, MSG_WITH_HELPER, new KafkaProducerListener());
        log.info(MSG_WITH_HELPER);
        return RSP;
    }

}

```

ConsumerService

```java
@Slf4j
@Service
public class ConsumerService {

    /**
     * 处理发送消息
     *
     * @param message 参数
     */
    @KafkaListener(id = "artisan", topics = "topic-artisan")
    public void receive(String message) {
        log.info("接受到消息{}", message);
    }
}
    
```

##  多Kafka 

DynamicKafkaController

```java
@Slf4j
@RestController
@RequestMapping("/kafka")
public class DynamicKafkaController {

    @Resource(name = "mwsKafkaTemplate")
    private KafkaTemplate mwsKafkaTemplate;

    @Resource(name = "sxmKafkaTemplate")
    private KafkaTemplate sxmKafkaTemplate;


    @GetMapping("/dSend")
    public String send() {
        mwsKafkaTemplate.send("topic-artisan", "message from mbws kafka starter");
        log.info("2mbws消息发送成功");
        return "2mbws消息发送成功";
    }


    @GetMapping("/dSend2sxm")
    public String send2SXM() {
        sxmKafkaTemplate.send("topic-artisan", "message from sxm kafka starter");
        log.info("2sxm消息发送成功");
        return "2sxm消息发送成功";
    }

}
```


DynamicConsumerService

```java
@Slf4j
@Service
public class DynamicConsumerService {

    /**
     * 处理发送消息
     *
     * @param message 参数
     */
    @KafkaListener(id = "mbws", topics = "topic-artisan",containerFactory = "mwsKafkaListenerContainerFactory")
    public void receive(String message) {
        log.info("MBWS KAFAK 接受到消息{}", message);
    }


    @KafkaListener(id = "sxm", topics = "topic-artisan",containerFactory = "sxmKafkaListenerContainerFactory")
    public void receiveFromSxm(String message) {
        log.info("SXM KAFKA接受到消息{}", message);
    }


}
    
```

