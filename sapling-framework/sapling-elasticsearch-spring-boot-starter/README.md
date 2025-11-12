# Sapling Elasticsearch Spring Boot Starter

基于 BBoss Elasticsearch 封装的 Spring Boot Starter，提供简化的 Elasticsearch 操作接口和注解驱动的索引映射管理。

## 功能特性

- 🚀 **自动配置**：开箱即用的 Spring Boot 自动配置
- 📝 **注解驱动**：通过注解自动生成 Elasticsearch 索引映射
- 🔧 **简化操作**：封装常用的 CRUD 操作，简化开发
- 🎯 **类型安全**：基于泛型的类型安全操作
- 📊 **灵活映射**：支持多种数据类型和自定义映射配置
- 🔍 **DSL 支持**：支持 XML 配置的 DSL 查询

## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml

<dependency>
    <groupId>com.sapling</groupId>
    <artifactId>sapling-elasticsearch-spring-boot-starter</artifactId>
    <version>${revision}</version>
</dependency>
```

### 2. 配置 Elasticsearch

在 `application.yml` 中配置 Elasticsearch 连接信息：

```yaml
spring:
  elasticsearch:
    bboss:
      elasticUser: elastic
      elasticPassword: your_password
      elasticsearch:
        rest:
          hostNames: 127.0.0.1:9200  # ES 集群地址，多个用逗号分隔
        dateFormat: yyyy.MM.dd
        timeZone: Asia/Shanghai
        ttl: 2d
        showTemplate: true
        discoverHost: false
      dslfile:
        refreshInterval: -1
      http:
        timeoutConnection: 50000
        timeoutSocket: 50000
        connectionRequestTimeout: 50000
        retryTime: 1
        maxTotal: 400
        defaultMaxPerRoute: 200

# 可选：自定义配置
sapling:
  es:
    bboss:
      enabled: true  # 是否启用，默认 true
```

### 3. 定义实体类

使用 `@ESDsl` 和 `@ESMapping` 注解定义实体类：

```java
package com.example.entity;

import annotations.com.sapling.framework.elasticsearch.ESDsl;
import annotations.com.sapling.framework.elasticsearch.ESMapping;
import enums.com.sapling.framework.elasticsearch.ESMappingType;
import lombok.Data;

@Data
@ESDsl(
        value = "esmapper/user.xml",  // DSL 配置文件路径
        indexName = "user_index",      // 索引名称
        indexType = "user"             // 索引类型（ES 7.x+ 可省略）
)
public class User {

   @ESMapping(ESMappingType.keyword)
   private String id;

   @ESMapping(
           value = ESMappingType.text,
           analyzer = "ik_max_word",  // 使用 IK 分词器
           boost = 2                   // 加权值
   )
   private String name;

   @ESMapping(ESMappingType._integer)
   private Integer age;

   @ESMapping(ESMappingType.keyword)
   private String email;

   @ESMapping(ESMappingType.date)
   private Date createTime;

   // 默认为 text 类型
   private String description;
}
```

### 4. 创建 Service

继承 `ElasticBaseService` 实现业务逻辑：

```java
package com.example.service;

import com.example.entity.User;
import service.com.sapling.framework.elasticsearch.ElasticBaseService;
import org.springframework.stereotype.Service;

@Service
public class UserElasticService extends ElasticBaseService<User> {

   // 可以添加自定义方法

}
```

### 5. 使用示例

```java
package com.example.controller;

import com.example.entity.User;
import com.example.service.UserElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserElasticService userElasticService;

    /**
     * 创建索引
     */
    @PostMapping("/createIndex")
    public String createIndex() {
        userElasticService.createIndex();
        return "索引创建成功";
    }

    /**
     * 删除索引
     */
    @DeleteMapping("/deleteIndex")
    public String deleteIndex() {
        return userElasticService.delIndex();
    }

    /**
     * 添加文档
     */
    @PostMapping("/add")
    public String addDocument(@RequestBody User user) {
        user.setCreateTime(new Date());
        return userElasticService.addDocument(user, true);
    }

    /**
     * 批量添加文档
     */
    @PostMapping("/addBatch")
    public String addDocuments(@RequestBody List<User> users) {
        return userElasticService.addDocuments(users, true);
    }

    /**
     * 根据 ID 获取文档
     */
    @GetMapping("/{id}")
    public User getDocument(@PathVariable String id) {
        return userElasticService.getDocument(id, User.class);
    }

    /**
     * 更新文档
     */
    @PutMapping("/{id}")
    public String updateDocument(@PathVariable String id, @RequestBody User user) {
        return userElasticService.updateDocument(id, user, true);
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{id}")
    public String deleteDocument(@PathVariable String id) {
        return userElasticService.delDocument(id, true);
    }

    /**
     * 批量删除文档
     */
    @DeleteMapping("/deleteBatch")
    public String deleteDocuments(@RequestBody String[] ids) {
        return userElasticService.delDocuments(ids, true);
    }
}
```

## 核心注解说明

### @ESDsl

用于标注实体类，定义索引的基本信息。

**属性：**

- `value`：DSL 配置文件路径（XML 格式）
- `indexName`：索引名称（必填）
- `indexType`：索引类型（ES 7.x+ 已废弃，可省略）

**示例：**

```java

@ESDsl(
        value = "esmapper/product.xml",
        indexName = "product_index",
        indexType = "product"
)
public class Product {
    // ...
}
```

### @ESMapping

用于标注字段，定义字段的映射类型和属性。

**属性：**

- `value`：映射类型（必填），参考 `ESMappingType` 枚举
- `boost`：加权值，默认为 1
- `index`：分词标识，默认为 "analyzed"
- `analyzer`：分词器，默认为 "standard"
- `fielddata`：是否支持聚合分组，默认为 false

**示例：**

```java
// keyword 类型（不分词）
@ESMapping(ESMappingType.keyword)
private String id;

// text 类型，使用 IK 分词器，加权为 2
@ESMapping(
        value = ESMappingType.text,
        analyzer = "ik_max_word",
        boost = 2
)
private String title;

// 支持聚合的字符串字段
@ESMapping(
        value = ESMappingType.text,
        fielddata = true
)
private String category;
```

## 支持的数据类型

`ESMappingType` 枚举支持以下 Elasticsearch 数据类型：

| 类型             | 说明                 | Java 类型映射                      |
|----------------|--------------------|--------------------------------|
| `text`         | 全文搜索               | String                         |
| `keyword`      | 精确匹配、排序、聚合         | String                         |
| `_byte`        | 字节型 (-128~127)     | byte, Byte                     |
| `_short`       | 短整型 (-32768~32767) | short, Short                   |
| `_integer`     | 整型                 | int, Integer                   |
| `_long`        | 长整型                | long, Long                     |
| `_float`       | 单精度浮点              | float, Float                   |
| `_doule`       | 双精度浮点              | double, Double                 |
| `half_float`   | 半精度浮点              | -                              |
| `scaled_float` | 缩放浮点               | -                              |
| `date`         | 日期时间               | Date, LocalDate, LocalDateTime |
| `_boolean`     | 布尔型                | boolean, Boolean               |
| `range`        | 范围类型               | -                              |
| `nested`       | 嵌套对象               | Object                         |
| `geo_point`    | 地理坐标               | -                              |
| `geo_shape`    | 地理地图               | -                              |
| `binary`       | 二进制                | -                              |
| `ip`           | IP 地址              | -                              |

## 核心 API

### ElasticBaseService

基础服务类，提供常用的 Elasticsearch 操作方法。

#### 索引管理

```java
// 自动创建索引（基于注解）
void createIndex()

// 通过 XML 创建索引
String createIndexByXml(String propertyName)

// 删除索引
String delIndex()
```

#### 文档操作

```java
// 添加单个文档
String addDocument(T t, Boolean refresh)

// 批量添加文档
String addDocuments(List<T> ts, Boolean refresh)

// 分页添加文档（需重写 pageDate 方法）
void addDocumentsOfPage(List<T> ts, Boolean refresh)

// 根据 ID 获取文档
T getDocument(String id, Class<T> clazz)

// 更新文档
String updateDocument(String id, T t, Boolean refresh)

// 删除单个文档
String delDocument(String id, Boolean refresh)

// 批量删除文档
String delDocuments(String[] ids, Boolean refresh)
```

**参数说明：**

- `refresh`：是否强制刷新索引，`true` 表示立即可见，`false` 表示异步刷新

## 高级用法

### 1. 自定义分页数据加载

如果需要使用 `addDocumentsOfPage` 方法，需要重写 `pageDate` 方法：

```java

@Service
public class ProductElasticService extends ElasticBaseService<Product> {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> pageDate(int start, int rows) {
        // 从数据库分页查询数据
        return productMapper.selectPage(start, rows);
    }

    /**
     * 全量同步数据到 ES
     */
    public void syncAllData() {
        addDocumentsOfPage(null, true);
    }
}
```

### 2. 嵌套对象映射

```java

@Data
@ESDsl(value = "esmapper/order.xml", indexName = "order_index")
public class Order {

    @ESMapping(ESMappingType.keyword)
    private String orderId;

    @ESMapping(ESMappingType._doule)
    private Double totalAmount;

    // 嵌套对象
    @ESMapping(ESMappingType.nested)
    private Address address;

    @Data
    public static class Address {
        @ESMapping(ESMappingType.keyword)
        private String province;

        @ESMapping(ESMappingType.keyword)
        private String city;

        @ESMapping(ESMappingType.text)
        private String detail;
    }
}
```

### 3. 使用 XML 配置 DSL 查询

在 `resources/esmapper/user.xml` 中定义 DSL：

```xml

<properties>
    <property name="searchUser">
        <![CDATA[
        {
            "query": {
                "bool": {
                    "must": [
                        #if($name)
                        {"match": {"name": "#[name]"}},
                        #end
                        #if($age)
                        {"term": {"age": #[age]}}
                        #end
                    ]
                }
            },
            "from": #[from],
            "size": #[size]
        }
        ]]>
    </property>
</properties>
```

在 Service 中使用：

```java

@Service
public class UserElasticService extends ElasticBaseService<User> {

    @Autowired
    private BBossESStarter bbossESStarter;

    public List<User> searchUsers(String name, Integer age, int from, int size) {
        ClientInterface restClient = bbossESStarter.getConfigRestClient(xmlPath);

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("age", age);
        params.put("from", from);
        params.put("size", size);

        ESDatas<User> response = restClient.searchList(
                indexName + "/_search",
                "searchUser",
                params,
                User.class
        );

        return response.getDatas();
    }
}
```

## 配置项说明

### 核心配置

| 配置项                                                       | 说明         | 默认值           |
|-----------------------------------------------------------|------------|---------------|
| `sapling.es.bboss.enabled`                                | 是否启用 ES 组件 | true          |
| `spring.elasticsearch.bboss.elasticUser`                  | ES 用户名     | -             |
| `spring.elasticsearch.bboss.elasticPassword`              | ES 密码      | -             |
| `spring.elasticsearch.bboss.elasticsearch.rest.hostNames` | ES 集群地址    | -             |
| `spring.elasticsearch.bboss.elasticsearch.dateFormat`     | 日期格式       | yyyy.MM.dd    |
| `spring.elasticsearch.bboss.elasticsearch.timeZone`       | 时区         | Asia/Shanghai |

### HTTP 连接池配置

| 配置项                                                  | 说明              | 默认值   |
|------------------------------------------------------|-----------------|-------|
| `spring.elasticsearch.bboss.http.timeoutConnection`  | 连接超时时间（毫秒）      | 50000 |
| `spring.elasticsearch.bboss.http.timeoutSocket`      | Socket 超时时间（毫秒） | 50000 |
| `spring.elasticsearch.bboss.http.maxTotal`           | 最大连接数           | 400   |
| `spring.elasticsearch.bboss.http.defaultMaxPerRoute` | 每个路由的最大连接数      | 200   |

## 注意事项

1. **索引名称规范**：建议使用小写字母和下划线，避免使用特殊字符
2. **分词器选择**：
    - 中文推荐使用 `ik_max_word` 或 `ik_smart`
    - 英文可使用 `standard` 或 `english`
3. **refresh 参数**：
    - 开发环境可设置为 `true`，保证数据立即可见
    - 生产环境建议设置为 `false`，提高性能
4. **索引映射**：索引创建后，映射结构不可修改，只能删除重建
5. **ES 版本兼容**：本组件基于 BBoss，支持 ES 5.x ~ 8.x 版本

## 依赖说明

本模块依赖以下核心组件：

- `bboss-elasticsearch-spring-boot-starter`：BBoss ES 核心库
- `bboss-elasticsearch-rest-jdbc`：BBoss JDBC 支持
- `fastjson`：JSON 序列化
- `hutool-all`：工具类库

## 常见问题

### 1. 连接失败

**问题**：无法连接到 Elasticsearch

**解决**：

- 检查 ES 服务是否启动
- 确认 `hostNames` 配置正确
- 检查网络连接和防火墙设置
- 验证用户名密码是否正确

### 2. 索引创建失败

**问题**：调用 `createIndex()` 报错

**解决**：

- 确认实体类上有 `@ESDsl` 注解
- 检查索引名称是否符合规范
- 查看 ES 日志获取详细错误信息

### 3. 中文分词不生效

**问题**：中文搜索结果不准确

**解决**：

- 确认已安装 IK 分词器插件
- 在 `@ESMapping` 中指定 `analyzer = "ik_max_word"`
- 重建索引使配置生效

## 示例项目

完整示例代码请参考：`sapling-module-system` 模块中的使用示例。

## 技术支持

如有问题，请提交 Issue 或联系开发团队。
