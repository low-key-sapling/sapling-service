# MBWS Kafka监听器实现文档

## 概述

本文档描述了MBWS Kafka监听器的实现，包括告警监听器和文件判断监听器，这些功能分别监听`mbws-alrm`和`mbws-fileJudge`主题的消息，并使用线程池进行异步处理。

## 功能特性

- 监听`mbws-alrm` Kafka主题（告警消息）
- 监听`mbws-fileJudge` Kafka主题（文件判断消息）
- 使用线程池异步处理消息，提高并发性能
- 支持手动确认消息，确保消息处理的可靠性
- 完整的错误处理和日志记录
- 支持JSON格式消息解析

## 架构设计

### 组件结构

```
endpoint-module-system/
├── endpoint-module-system-domain/
│   └── dto/
│       ├── MbwsAlarmMessageDto.java          # 告警消息DTO
│       └── MbwsFileJudgeMessageDto.java      # 文件判断消息DTO
├── endpoint-module-system-infrastructure/
│   └── common/constants/
│       └── KafkaTopicConstants.java          # Kafka主题常量
├── endpoint-module-system-app/
│   └── service/
│       ├── MbwsAlarmMessageService.java      # 告警消息处理服务
│       └── MbwsFileJudgeMessageService.java  # 文件判断消息处理服务
└── endpoint-module-system-adapter/
    └── terminal/kafka/
        ├── config/
        │   ├── KafkaConfig.java              # Kafka配置
        │   └── KafkaThreadPoolConfig.java    # 线程池配置
        └── listener/
            ├── MbwsAlarmKafkaListener.java   # 告警Kafka监听器
            └── MbwsFileJudgeKafkaListener.java # 文件判断Kafka监听器
```

### 核心组件说明

#### 1. KafkaTopicConstants
- 定义Kafka主题常量
- 告警主题：`mbws-alrm`
- 文件判断主题：`mbws-fileJudge`

#### 2. 消息DTO
- **MbwsAlarmMessageDto**：告警消息数据传输对象，包含消息ID、告警类型、级别、内容等字段
- **MbwsFileJudgeMessageDto**：文件判断消息数据传输对象，包含文件路径、判断结果、风险等级等字段

#### 3. 消息处理服务
- **MbwsAlarmMessageService**：告警消息业务处理服务，负责告警消息解析和业务逻辑处理
- **MbwsFileJudgeMessageService**：文件判断消息业务处理服务，负责文件判断结果处理和风险管控

#### 4. KafkaThreadPoolConfig
- 线程池配置类
- 核心线程数：5
- 最大线程数：20
- 队列容量：100
- 拒绝策略：CallerRunsPolicy

#### 5. Kafka监听器
- **MbwsAlarmKafkaListener**：监听`mbws-alrm`主题的告警消息
- **MbwsFileJudgeKafkaListener**：监听`mbws-fileJudge`主题的文件判断消息
- 将消息处理任务提交到线程池，支持手动确认消息

## 配置说明

### Kafka配置
在`application-kafka.yaml`中已配置：
- 消费者组：`sapling-group`
- 手动确认模式：`manual`
- 并发数：12

### 线程池配置
- 核心线程数：5
- 最大线程数：20
- 队列容量：100
- 线程名前缀：`kafka-msg-`
- 拒绝策略：由调用线程处理

## 使用方式

### 1. 启动应用
应用启动后会自动注册Kafka监听器，开始监听相应主题。

### 2. 告警消息格式
支持JSON格式的告警消息：
```json
{
  "messageId": "123456789",
  "alarmType": "SYSTEM_ERROR",
  "alarmLevel": "HIGH",
  "alarmContent": "系统异常告警",
  "alarmTime": "2024-01-01T10:00:00",
  "deviceId": "DEV001",
  "deviceName": "设备001"
}
```

### 3. 文件判断消息格式
支持JSON格式的文件判断消息：
```json
{
  "messageId": "987654321",
  "filePath": "/path/to/file.exe",
  "fileName": "file.exe",
  "fileSize": 1024000,
  "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
  "judgeResult": "RISK",
  "judgeType": "MALWARE_SCAN",
  "riskLevel": "HIGH",
  "deviceId": "DEV001",
  "deviceName": "设备001",
  "userId": "USER001",
  "userName": "用户001",
  "judgeTime": "2024-01-01T10:00:00"
}
```

### 4. 扩展处理逻辑
在相应的Service类中添加具体的业务处理逻辑：

#### 告警消息处理
在`MbwsAlarmMessageService.handleAlarmMessage()`方法中添加：
- 数据库存储
- 通知发送
- 告警处理流程触发

#### 文件判断消息处理
在`MbwsFileJudgeMessageService.handleFileJudgeMessage()`方法中添加：
- 判断结果存储
- 风险文件处理
- 安全策略执行

## 监控和日志

### 日志级别
- INFO：正常消息处理流程
- WARN：消息格式异常、发现风险文件
- ERROR：处理失败

### 关键日志
- 消息接收：记录Topic、Partition、Offset信息
- 处理开始：记录消息内容
- 处理完成：记录处理结果
- 处理失败：记录错误详情
- 风险文件：记录风险文件详情

## 性能优化

### 线程池优化
- 根据实际消息量调整核心线程数和最大线程数
- 监控队列使用情况，适当调整队列容量
- 根据消息处理时间调整线程空闲时间

### 消费者优化
- 调整`max-poll-records`控制批量消费数量
- 调整`fetch-min-size`和`fetch-max-wait`优化网络IO
- 根据业务需求调整并发数

## 注意事项

1. **消息确认**：当前实现在处理完成后确认消息，如需重试机制请修改确认逻辑
2. **异常处理**：处理失败的消息会被确认，避免重复消费，根据业务需求调整
3. **线程安全**：消息处理服务需要保证线程安全
4. **资源管理**：注意监控线程池使用情况，避免资源耗尽
5. **风险文件处理**：文件判断为风险时需要及时处理，避免安全威胁

## 扩展建议

1. **消息重试机制**：实现失败消息的重试逻辑
2. **死信队列**：处理无法处理的消息
3. **监控指标**：添加消息处理的监控指标
4. **配置外部化**：将线程池参数配置外部化
5. **消息过滤**：根据消息类型进行过滤处理
6. **风险等级处理**：根据不同风险等级执行不同的处理策略
7. **文件隔离**：对高风险文件实施自动隔离
8. **告警升级**：实现告警等级自动升级机制

## 主题说明

### mbws-alrm 主题
- 用途：接收MBWS系统产生的各类告警消息
- 消息类型：系统告警、设备告警、安全告警等
- 处理方式：实时处理，及时响应

### mbws-fileJudge 主题  
- 用途：接收MBWS系统的文件判断结果
- 消息类型：文件安全性判断、恶意软件检测结果等
- 处理方式：根据判断结果执行相应的安全策略