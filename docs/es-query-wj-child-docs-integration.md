# EsQueryWJChildDocsCmp 主机信息集成文档

## 概述

本文档描述了在`EsQueryWJChildDocsCmp`类中集成主机查询功能的实现。该功能从文件信息中提取hostId字段集合，通过批量查询获取主机信息，并将主机信息合并到文件信息中。

## 功能特性

- **批量查询优化**: 使用`chkHostService.listByHostIds()`进行批量查询，每1000个ID为一批
- **字段名兼容**: 支持多种可能的hostId字段名（hostId、host_id、deviceId等）
- **数据合并**: 将主机信息合并到文件信息中，提供完整的上下文
- **异常容错**: 完整的异常处理，单个文件信息异常不影响整体处理
- **性能优化**: 去重处理，避免重复查询相同的hostId

## 实现流程

### 1. 数据流程图

```
fileCodes (输入)
    ↓
transformGateway.getWJFileInfoByFileCode()
    ↓
提取hostId字段集合
    ↓
chkHostService.listByHostIds() (批量查询)
    ↓
构建hostId映射表
    ↓
合并文件信息和主机信息
    ↓
enrichedFileInfoList (输出)
```

### 2. 核心方法说明

#### extractHostIds()
- **功能**: 从文件信息列表中提取hostId字段集合
- **去重**: 自动去除重复的hostId
- **容错**: 处理无效或缺失的hostId

#### extractHostIdFromFileInfo()
- **功能**: 从单个文件信息中提取hostId
- **字段兼容**: 支持多种字段名格式
- **类型转换**: 支持Number和String类型的hostId

#### enrichFileInfoWithHostInfo()
- **功能**: 将主机信息合并到文件信息中
- **数据结构**: 既提供完整的hostInfo对象，也提供扁平化的关键字段

## 支持的hostId字段名

系统会按顺序尝试以下字段名：

1. `hostId`
2. `host_id`
3. `deviceId`
4. `device_id`
5. `machineId`
6. `machine_id`

## 输入输出示例

### 输入数据结构

```json
// transformGateway.getWJFileInfoByFileCode() 返回的数据
[
  {
    "fileCode": "FILE001",
    "fileName": "document.pdf",
    "hostId": 1001,
    "filePath": "/data/files/document.pdf",
    "fileSize": 1024000
  },
  {
    "fileCode": "FILE002",
    "fileName": "image.jpg",
    "host_id": "1002",
    "filePath": "/data/images/image.jpg",
    "fileSize": 512000
  }
]
```

### 输出数据结构

```json
// 合并主机信息后的数据
[
  {
    "fileCode": "FILE001",
    "fileName": "document.pdf",
    "hostId": 1001,
    "filePath": "/data/files/document.pdf",
    "fileSize": 1024000,
    
    // 完整的主机信息对象
    "hostInfo": {
      "id": 1001,
      "hostName": "server-001",
      "hostIp": "192.168.1.100",
      "status": 1,
      "statusDesc": "在线",
      "hostType": 1,
      "hostTypeDesc": "服务器",
      "orgId": "ORG001",
      "orgName": "技术部",
      "riskLevel": 2,
      "riskLevelDesc": "中"
    },
    
    // 扁平化的关键字段（便于直接使用）
    "hostName": "server-001",
    "hostIp": "192.168.1.100",
    "hostStatus": 1,
    "hostStatusDesc": "在线",
    "hostType": 1,
    "hostTypeDesc": "服务器",
    "orgId": "ORG001",
    "orgName": "技术部",
    "riskLevel": 2,
    "riskLevelDesc": "中"
  },
  {
    "fileCode": "FILE002",
    "fileName": "image.jpg",
    "host_id": "1002",
    "filePath": "/data/images/image.jpg",
    "fileSize": 512000,
    
    "hostInfo": {
      "id": 1002,
      "hostName": "workstation-002",
      "hostIp": "192.168.1.200",
      "status": 0,
      "statusDesc": "离线",
      "hostType": 2,
      "hostTypeDesc": "终端",
      "orgId": "ORG002",
      "orgName": "业务部",
      "riskLevel": 1,
      "riskLevelDesc": "低"
    },
    
    "hostName": "workstation-002",
    "hostIp": "192.168.1.200",
    "hostStatus": 0,
    "hostStatusDesc": "离线",
    "hostType": 2,
    "hostTypeDesc": "终端",
    "orgId": "ORG002",
    "orgName": "业务部",
    "riskLevel": 1,
    "riskLevelDesc": "低"
  }
]
```

### 无主机信息的情况

```json
// 当找不到对应主机信息时
{
  "fileCode": "FILE003",
  "fileName": "unknown.txt",
  "hostId": 9999,
  "filePath": "/data/unknown.txt",
  "fileSize": 1024,
  
  "hostInfo": null,
  "hostFound": false
}
```

## 性能优化

### 1. 批量查询
- 使用`listByHostIds()`进行批量查询
- 自动分批处理，每1000个ID为一批
- 避免N+1查询问题

### 2. 去重处理
```java
.distinct()  // 自动去除重复的hostId
```

### 3. 内存优化
- 使用Stream API进行流式处理
- 构建Map映射表，O(1)时间复杂度查找

### 4. 异常隔离
- 单个文件信息处理异常不影响其他文件
- 提供详细的日志记录便于问题排查

## 日志输出示例

```
INFO  - 开始查询WJ文件信息，fileCodes数量: 100
INFO  - 查询到WJ文件信息数量: 95
INFO  - 提取到hostId数量: 45
INFO  - 开始批量查询主机信息，ID数量: 45
DEBUG - 处理第1批，范围: 0-44, 数量: 45
DEBUG - 第1批查询完成，查询到43条记录
INFO  - 批量查询主机信息完成，返回43条记录
INFO  - 查询到主机信息数量: 43
INFO  - 完成文件信息与主机信息合并，最终数量: 95
```

## 错误处理

### 1. 输入验证
- fileCodes为空时返回空列表
- 文件信息查询失败时返回空列表

### 2. hostId提取异常
- 字段不存在：记录debug日志，跳过该文件
- 格式错误：记录warn日志，跳过该文件
- 类型不支持：记录warn日志，跳过该文件

### 3. 主机查询异常
- 查询失败：抛出异常，由上层处理
- 部分查询失败：记录错误日志，继续处理成功的部分

### 4. 数据合并异常
- 单个文件合并失败：记录错误日志，返回原始文件信息
- 整体合并失败：抛出异常

## 扩展建议

### 1. 缓存优化
```java
// 可以添加本地缓存避免重复查询
@Cacheable(value = "hostCache", key = "#hostIds.hashCode()")
public List<ChkHostDto> listByHostIds(List<Long> hostIds) {
    // 实现
}
```

### 2. 异步处理
```java
// 对于大量数据可以考虑异步处理
@Async
public CompletableFuture<List<ChkHostDto>> listByHostIdsAsync(List<Long> hostIds) {
    // 实现
}
```

### 3. 字段配置化
```java
// 将支持的hostId字段名配置化
@Value("${host.id.field.names:hostId,host_id,deviceId,device_id}")
private String[] hostIdFieldNames;
```

### 4. 监控指标
- 查询耗时监控
- 成功率监控
- 数据量监控

## 使用注意事项

1. **数据一致性**: 确保文件信息中的hostId与主机表中的ID一致
2. **性能考虑**: 大量文件信息时注意内存使用和查询性能
3. **字段命名**: 建议统一使用`hostId`字段名
4. **空值处理**: 业务逻辑中需要处理hostInfo为null的情况
5. **日志级别**: 生产环境建议将debug日志关闭以提高性能

## 测试建议

### 1. 单元测试
- 测试hostId提取逻辑
- 测试数据合并逻辑
- 测试异常处理

### 2. 集成测试
- 测试完整的数据流程
- 测试大量数据的性能
- 测试异常场景的处理

### 3. 性能测试
- 测试不同数据量下的性能表现
- 测试内存使用情况
- 测试并发处理能力