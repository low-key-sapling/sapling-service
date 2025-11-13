# Sapling MyBatis Plus Boot Starter

基于 MyBatis-Plus 3.5.x 封装的 Spring Boot Starter，提供增强的数据库操作能力和多数据库适配支持。

## 设计理念

**只做增强不做改变，为简化开发、提高效率而生**

本组件完全兼容 MyBatis-Plus 的使用方式，在此基础上提供了更多实用功能：
- 🚀 增强型 Mapper 接口，提供更多便捷方法
- 🔍 增强型条件构造器，支持智能判空
- 🗄️ 多数据库自动适配，支持国产数据库
- 📄 分页查询封装，简化分页操作
- ⚙️ 自动字段填充，支持创建时间/更新时间
- 🛠️ 代码生成器集成，配合 MybatisX 插件快速开发

## 支持的数据库

支持任何能使用 MyBatis 进行 CRUD 且支持标准 SQL 的数据库：

### 主流数据库
- MySQL、MariaDB
- Oracle、Oracle 12c+
- SQL Server 2005、SQL Server 2012+
- PostgreSQL
- DB2、H2、HSQL、SQLite

### 国产数据库
- 达梦数据库（DM）
- 虚谷数据库（XuGu）
- 人大金仓数据库（KingBase ES）
- 南大通用数据库（GBase）
- 神通数据库（Oscar）
- 瀚高数据库（HighGo）

### 其他数据库
- Phoenix HBase
- Gauss、ClickHouse
- Sybase、OceanBase
- Firebird、Cubrid
- Goldilocks、csiidb


## 快速开始

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.sapling</groupId>
    <artifactId>sapling-mybatis-plus-boot-starter</artifactId>
    <version>${revision}</version>
</dependency>

<!-- 添加数据库驱动，以 MySQL 为例 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

### 2. 配置数据源

在 `application.yml` 或 `application.properties` 中配置：

```yaml
# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

# Sapling MyBatis 配置
sapling:
  mybatis:
    # 是否开启默认字段自动填充（createTime、updateTime）
    common-field-value-auto-fill-enable: false
    # Mapper 扫描根路径（等同于 @MapperScan）
    base-package: com.example.mapper

# MyBatis Plus 配置（可选）
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```


### 3. 创建实体类

```java
package com.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String email;
    
    private Integer age;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

### 4. 创建 Mapper 接口

**推荐使用增强型 BaseMapperX：**

```java
package com.example.mapper;

import com.example.entity.User;
import com.sapling.frame.mybatis.core.mapper.BaseMapperX;

/**
 * User Mapper 接口
 * 继承 BaseMapperX 获得增强功能
 */
public interface UserMapper extends BaseMapperX<User> {
    // 无需编写任何代码，即可使用增强方法
}
```


### 5. 使用示例

```java
package com.example.service;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.sapling.frame.mybatis.core.pojo.PageParam;
import com.sapling.frame.mybatis.core.pojo.PageResult;
import com.sapling.frame.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 根据 ID 查询
     */
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 根据单个字段查询
     */
    public User getByUsername(String username) {
        return userMapper.selectOne("username", username);
    }
    
    /**
     * 根据多个字段查询（Lambda 方式）
     */
    public User getByUsernameAndEmail(String username, String email) {
        return userMapper.selectOne(User::getUsername, username, User::getEmail, email);
    }
    
    /**
     * 查询列表
     */
    public List<User> listAll() {
        return userMapper.selectList();
    }
    
    /**
     * 根据字段查询列表
     */
    public List<User> listByAge(Integer age) {
        return userMapper.selectList(User::getAge, age);
    }
    
    /**
     * 统计数量
     */
    public Long count() {
        return userMapper.selectCount();
    }
    
    /**
     * 根据条件统计
     */
    public Long countByAge(Integer age) {
        return userMapper.selectCount(User::getAge, age);
    }
    
    /**
     * 分页查询（使用增强条件构造器）
     */
    public PageResult<User> page(PageParam pageParam, String username, Integer minAge) {
        return userMapper.selectPage(pageParam, new LambdaQueryWrapperX<User>()
                .likeIfPresent(User::getUsername, username)  // 智能判空
                .geIfPresent(User::getAge, minAge)           // 智能判空
                .orderByDesc(User::getCreateTime));
    }
    
    /**
     * 新增
     */
    public void save(User user) {
        userMapper.insert(user);
    }
    
    /**
     * 更新
     */
    public void update(User user) {
        userMapper.updateById(user);
    }
    
    /**
     * 删除
     */
    public void delete(Long id) {
        userMapper.deleteById(id);
    }
}
```


## 核心功能详解

### 1. 增强型 Mapper - BaseMapperX

`BaseMapperX` 在 MyBatis-Plus 的 `BaseMapper` 基础上提供了更多便捷方法。

#### 单条查询

```java
// 根据单个字段查询（字符串方式）
User user = userMapper.selectOne("username", "admin");

// 根据单个字段查询（Lambda 方式，类型安全）
User user = userMapper.selectOne(User::getUsername, "admin");

// 根据两个字段查询
User user = userMapper.selectOne("username", "admin", "email", "admin@example.com");

// 根据两个字段查询（Lambda 方式）
User user = userMapper.selectOne(User::getUsername, "admin", User::getEmail, "admin@example.com");
```

#### 列表查询

```java
// 查询所有
List<User> users = userMapper.selectList();

// 根据单个字段查询列表
List<User> users = userMapper.selectList("status", 1);

// 根据单个字段查询列表（Lambda 方式）
List<User> users = userMapper.selectList(User::getStatus, 1);

// 根据字段 IN 查询
List<Long> ids = Arrays.asList(1L, 2L, 3L);
List<User> users = userMapper.selectList("id", ids);

// 根据字段 IN 查询（Lambda 方式）
List<User> users = userMapper.selectList(User::getId, ids);
```

#### 统计查询

```java
// 统计所有
Long count = userMapper.selectCount();

// 根据条件统计
Long count = userMapper.selectCount("status", 1);

// 根据条件统计（Lambda 方式）
Long count = userMapper.selectCount(User::getStatus, 1);
```

#### 分页查询

```java
// 创建分页参数
PageParam pageParam = new PageParam();
pageParam.setPageNo(1);
pageParam.setPageSize(10);

// 分页查询
PageResult<User> pageResult = userMapper.selectPage(pageParam, 
    new LambdaQueryWrapperX<User>()
        .eq(User::getStatus, 1)
        .orderByDesc(User::getCreateTime));

// 获取结果
List<User> list = pageResult.getList();
Long total = pageResult.getTotal();
```

#### 批量操作

```java
// 批量插入
List<User> users = Arrays.asList(user1, user2, user3);
userMapper.insertBatch(users);

// 批量插入（使用增强方法，性能更好）
userMapper.insertBatchSomeColumn(users);

// 批量更新
userMapper.updateBatch(updateUser);

// 批量更新（指定条件）
userMapper.updateBatchById(users, new LambdaQueryWrapper<User>()
    .eq(User::getStatus, 1));
```


### 2. 增强型条件构造器

#### LambdaQueryWrapperX

提供智能判空的条件拼接方法，避免空值条件污染 SQL。

```java
import com.sapling.frame.mybatis.core.query.LambdaQueryWrapperX;

// 传统方式：需要手动判空
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
if (StringUtils.hasText(username)) {
    wrapper.like(User::getUsername, username);
}
if (minAge != null) {
    wrapper.ge(User::getAge, minAge);
}

// 增强方式：自动判空
LambdaQueryWrapperX<User> wrapperX = new LambdaQueryWrapperX<User>()
    .likeIfPresent(User::getUsername, username)  // 自动判空
    .geIfPresent(User::getAge, minAge)           // 自动判空
    .orderByDesc(User::getCreateTime);
```

**支持的智能判空方法：**

| 方法 | 说明 | 判空规则 |
|------|------|----------|
| `eqIfPresent` | 等于 | 值不为 null |
| `neIfPresent` | 不等于 | 值不为 null |
| `gtIfPresent` | 大于 | 值不为 null |
| `geIfPresent` | 大于等于 | 值不为 null |
| `ltIfPresent` | 小于 | 值不为 null |
| `leIfPresent` | 小于等于 | 值不为 null |
| `likeIfPresent` | 模糊查询 | 字符串不为空 |
| `inIfPresent` | IN 查询 | 集合/数组不为空 |
| `betweenIfPresent` | 区间查询 | 至少一个值不为 null |

**使用示例：**

```java
// 复杂查询示例
public PageResult<User> searchUsers(UserSearchDTO dto, PageParam pageParam) {
    return userMapper.selectPage(pageParam, new LambdaQueryWrapperX<User>()
        .likeIfPresent(User::getUsername, dto.getUsername())
        .likeIfPresent(User::getEmail, dto.getEmail())
        .eqIfPresent(User::getStatus, dto.getStatus())
        .geIfPresent(User::getAge, dto.getMinAge())
        .leIfPresent(User::getAge, dto.getMaxAge())
        .betweenIfPresent(User::getCreateTime, dto.getStartTime(), dto.getEndTime())
        .inIfPresent(User::getDeptId, dto.getDeptIds())
        .orderByDesc(User::getCreateTime));
}
```

#### QueryWrapperX

字符串方式的增强条件构造器，用法与 `LambdaQueryWrapperX` 类似：

```java
import com.sapling.frame.mybatis.core.query.QueryWrapperX;

QueryWrapperX<User> wrapper = new QueryWrapperX<User>()
    .likeIfPresent("username", username)
    .eqIfPresent("status", status)
    .geIfPresent("age", minAge)
    .orderByDesc("create_time");
```


### 3. 分页查询封装

#### PageParam - 分页参数

```java
import com.sapling.frame.mybatis.core.pojo.PageParam;

PageParam pageParam = new PageParam();
pageParam.setPageNo(1);      // 页码，默认 1
pageParam.setPageSize(10);   // 每页条数，默认 10

// 支持参数校验
// @NotNull 页码不能为空
// @Min(1) 页码最小值为 1
// @Max(100) 每页条数最大值为 100
```

#### PageResult - 分页结果

```java
import com.sapling.frame.mybatis.core.pojo.PageResult;

PageResult<User> result = userMapper.selectPage(pageParam, wrapper);

List<User> list = result.getList();   // 数据列表
Long total = result.getTotal();       // 总记录数

// 创建空结果
PageResult<User> empty = PageResult.empty();
PageResult<User> emptyWithTotal = PageResult.empty(100L);
```

#### 完整分页示例

```java
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserMapper userMapper;
    
    @GetMapping("/page")
    public PageResult<User> page(@Valid PageParam pageParam, 
                                   @RequestParam(required = false) String username,
                                   @RequestParam(required = false) Integer status) {
        return userMapper.selectPage(pageParam, new LambdaQueryWrapperX<User>()
            .likeIfPresent(User::getUsername, username)
            .eqIfPresent(User::getStatus, status)
            .orderByDesc(User::getCreateTime));
    }
}
```


### 4. 多数据库适配

#### 自动识别数据库类型

本组件基于 JDBC URL 自动识别数据库类型，无需手动配置。

**识别规则示例：**

```yaml
# MySQL
spring.datasource.url: jdbc:mysql://localhost:3306/db

# Oracle
spring.datasource.url: jdbc:oracle:thin:@localhost:1521:orcl

# PostgreSQL
spring.datasource.url: jdbc:postgresql://localhost:5432/db

# SQL Server
spring.datasource.url: jdbc:sqlserver://localhost:1433;database=db

# 达梦数据库
spring.datasource.url: jdbc:dm://localhost:5236/db

# 人大金仓
spring.datasource.url: jdbc:kingbase8://localhost:54321/db

# 神通数据库
spring.datasource.url: jdbc:oscar://localhost:2003/db
```

#### 使用 databaseId 适配不同数据库

在 Mapper XML 中使用 `databaseId` 属性编写特定数据库的 SQL：

```xml
<!-- UserMapper.xml -->
<mapper namespace="com.example.mapper.UserMapper">
    
    <!-- MySQL 版本 -->
    <select id="selectUserList" resultType="User" databaseId="mysql">
        SELECT * FROM sys_user
        WHERE deleted = 0
        LIMIT #{offset}, #{limit}
    </select>
    
    <!-- Oracle 版本 -->
    <select id="selectUserList" resultType="User" databaseId="oracle">
        SELECT * FROM sys_user
        WHERE deleted = 0
        AND ROWNUM &lt;= #{limit}
    </select>
    
    <!-- PostgreSQL 版本 -->
    <select id="selectUserList" resultType="User" databaseId="postgresql">
        SELECT * FROM sys_user
        WHERE deleted = 0
        LIMIT #{limit} OFFSET #{offset}
    </select>
    
    <!-- 达梦数据库版本 -->
    <select id="selectUserList" resultType="User" databaseId="dm">
        SELECT * FROM sys_user
        WHERE deleted = 0
        LIMIT #{offset}, #{limit}
    </select>
    
    <!-- 通用版本（没有 databaseId 时使用） -->
    <select id="selectUserList" resultType="User">
        SELECT * FROM sys_user
        WHERE deleted = 0
    </select>
    
</mapper>
```


#### DatabaseId 字典表

| databaseId | 数据库 | JDBC URL 关键字 |
|------------|--------|----------------|
| mysql | MySQL | `:mysql:` |
| mariadb | MariaDB | `:mariadb:` |
| oracle | Oracle 11g 及以下 | `:oracle:` |
| oracle12c | Oracle 12c+ | `:oracle:` |
| sqlserver2005 | SQL Server 2005 | `:sqlserver:` |
| sqlserver | SQL Server 2012+ | `:sqlserver2012:` |
| postgresql | PostgreSQL | `:postgresql:` |
| db2 | DB2 | `:db2:` |
| h2 | H2 | `:h2:` |
| hsql | HSQL | `:hsqldb:` |
| sqlite | SQLite | `:sqlite:` |
| dm | 达梦数据库 | `:dm:` |
| xugu | 虚谷数据库 | `:xugu:` |
| kingbasees | 人大金仓 | `:kingbase:` |
| phoenix | Phoenix HBase | `:phoenix:` |
| zenith | Gauss | `:zenith:` |
| clickhouse | ClickHouse | `:clickhouse:` |
| gbase | 南大通用 | `:gbase:` |
| oscar | 神通数据库 | `:oscar:` |
| sybase | Sybase ASE | `:sybase:` |
| oceanbase | OceanBase | `:oceanbase:` |
| highgo | 瀚高数据库 | `:highgo:` |
| cubrid | Cubrid | `:cubrid:` |
| goldilocks | Goldilocks | `:goldilocks:` |
| csiidb | csiidb | `:csiidb:` |
| other | 其他数据库 | - |


## 配置说明

### 核心配置项

```yaml
sapling:
  mybatis:
    # 是否开启默认字段自动填充，默认 false
    # 开启后，会自动在执行 insert 和 update 时填充 createTime 和 updateTime
    common-field-value-auto-fill-enable: false
    
    # Mapper 扫描根路径，等同于 @MapperScan
    base-package: com.example.mapper
```

### MyBatis-Plus 常用配置

```yaml
mybatis-plus:
  # 配置
  configuration:
    # 驼峰转下划线
    map-underscore-to-camel-case: true
    # 日志实现
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    # 缓存
    cache-enabled: true
    # 懒加载
    lazy-loading-enabled: true
    
  # 全局配置
  global-config:
    # 数据库配置
    db-config:
      # 主键策略：AUTO 自增、ASSIGN_ID 雪花算法、ASSIGN_UUID UUID
      id-type: AUTO
      # 表名前缀
      table-prefix: sys_
      # 逻辑删除字段
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
    
    # Banner
    banner: false
  
  # Mapper XML 文件位置
  mapper-locations: classpath*:/mapper/**/*.xml
  
  # 实体类扫描路径
  type-aliases-package: com.example.entity
```

### 自动字段填充配置

如果开启了 `common-field-value-auto-fill-enable`，需要在实体类中配置：

```java
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
public class User {
    
    // 插入时自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // 插入和更新时自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```


## 开发流程推荐

### 标准开发流程

1. **使用代码生成工具生成基础代码**
   - 推荐使用 MybatisX IDEA 插件（见下文）
   - 自动生成 Entity、Mapper、Mapper.xml

2. **优先使用通用方法**
   - 使用 MyBatis-Plus 提供的 CRUD 通用接口
   - 使用增强型条件构造器组装 SQL

3. **复杂 SQL 使用 XML**
   - 无法通过通用方法实现的复杂 SQL
   - 在 Mapper.xml 中编写，配合 `databaseId` 适配多数据库

### 代码分层建议

```
com.example
├── entity          # 实体类
│   └── User.java
├── mapper          # Mapper 接口
│   └── UserMapper.java
├── service         # 业务层
│   ├── UserService.java
│   └── impl
│       └── UserServiceImpl.java
└── controller      # 控制层
    └── UserController.java

resources
└── mapper          # Mapper XML
    └── UserMapper.xml
```


## MybatisX 快速开发插件

MybatisX 是一款基于 IDEA 的快速开发插件，极大提升开发效率。

### 安装方法

1. 打开 IDEA
2. 进入 `File` -> `Settings` -> `Plugins` -> `Marketplace`
3. 搜索 `MybatisX` 并安装
4. 重启 IDEA

### 主要功能

#### 1. XML 与 Mapper 接口跳转

- 在 Mapper 接口方法上点击图标，快速跳转到对应的 XML
- 在 XML 的 SQL 语句上点击图标，快速跳转到 Mapper 方法
- 快捷键：`Ctrl + Alt + B`（Windows）/ `Cmd + Alt + B`（Mac）

#### 2. 代码生成器

**前提条件：** 需要先在 IDEA 中配置数据库连接（Database 工具窗口）

**使用步骤：**

1. 在 Database 工具窗口中，右键点击表
2. 选择 `MybatisX-Generator`
3. 配置生成选项：
   - **module path**: 选择模块路径
   - **base package**: 基础包名
   - **relative package**: 相对包路径
   - **encoding**: 编码格式（UTF-8）
   - **annotation**: 注解类型（MyBatis-Plus 3）
   - **template**: 模板选择（推荐 mybatis-plus3）
   - **options**: 
     - ✅ Comment
     - ✅ Lombok
     - ✅ Actual Column
     - ✅ JSR310: Date and Time API
4. 点击 `Finish` 生成代码

**生成的文件：**
- Entity 实体类
- Mapper 接口
- Mapper.xml 文件
- Service 接口（可选）
- ServiceImpl 实现类（可选）


#### 3. 自定义代码模板

**修改 Mapper 模板使用 BaseMapperX：**

1. 找到模板配置目录：
   - `Scratches and Consoles` -> `Extensions` -> `MybatisX`

2. 找到或创建 `mapper.java.ftl` 模板文件

3. 修改模板内容：

```java
package ${mapperInterface.packageName};

import ${tableClass.fullClassName};
import com.sapling.frame.mybatis.core.mapper.BaseMapperX;

/**
 * @author ${author!}
 * @description 针对表【${tableClass.tableName}<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】的数据库操作Mapper
 * @createDate ${.now?string('yyyy-MM-dd HH:mm:ss')}
 * @Entity ${tableClass.fullClassName}
 */
public interface ${mapperInterface.fileName} extends BaseMapperX<${tableClass.shortClassName}> {

}
```

4. 重置默认模板：
   - 右键点击 `MybatisX` 目录
   - 选择 `Restore Default Extensions`

**模板变量说明：**

| 变量 | 说明 |
|------|------|
| `${tableClass.fullClassName}` | 类的全称（包括包名） |
| `${tableClass.shortClassName}` | 类的简称 |
| `${tableClass.tableName}` | 表名 |
| `${tableClass.pkFields}` | 表的所有主键字段 |
| `${tableClass.allFields}` | 表的所有字段 |
| `${tableClass.baseFields}` | 排除主键和 blob 的所有字段 |
| `${tableClass.remark}` | 表注释 |
| `${field.fieldName}` | 字段名称 |
| `${field.columnName}` | 列名称 |
| `${field.jdbcType}` | JDBC 类型 |
| `${field.shortTypeName}` | Java 类型短名称 |
| `${field.remark}` | 字段注释 |


#### 4. JPA 风格方法生成

在 Mapper 接口中，输入方法名前缀，自动生成对应的 SQL 和方法签名。

**前提条件：**
- Mapper 继承 `BaseMapper` 或 `BaseMapperX`
- 实体类有 JPA 注解或 MyBatis-Plus 注解
- 或在 Mapper 类上添加注释：`@Entity com.example.entity.User`

**支持的方法前缀：**

| 前缀 | 说明 | 示例 |
|------|------|------|
| `select` | 查询 | `selectByUsername` |
| `find` | 查询 | `findByUsernameAndAge` |
| `get` | 查询 | `getByEmail` |
| `query` | 查询 | `queryByStatus` |
| `count` | 统计 | `countByStatus` |
| `insert` | 插入 | `insertSelective` |
| `update` | 更新 | `updateByUsername` |
| `delete` | 删除 | `deleteByUsername` |

**使用示例：**

```java
public interface UserMapper extends BaseMapperX<User> {
    
    // 输入 selectByUsername，自动生成：
    User selectByUsername(@Param("username") String username);
    
    // 输入 findByUsernameAndAge，自动生成：
    List<User> findByUsernameAndAge(@Param("username") String username, 
                                      @Param("age") Integer age);
    
    // 输入 countByStatus，自动生成：
    Long countByStatus(@Param("status") Integer status);
    
    // 输入 updateAgeById，自动生成：
    int updateAgeById(@Param("age") Integer age, @Param("id") Long id);
    
    // 输入 deleteByUsername，自动生成：
    int deleteByUsername(@Param("username") String username);
}
```

对应的 XML 会自动生成：

```xml
<select id="selectByUsername" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM sys_user
    WHERE username = #{username}
</select>

<select id="findByUsernameAndAge" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM sys_user
    WHERE username = #{username} AND age = #{age}
</select>
```


## 最佳实践

### 1. 实体类设计

```java
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")  // 指定表名
public class User {
    
    /**
     * 主键策略：
     * AUTO: 数据库自增
     * ASSIGN_ID: 雪花算法（Long 类型）
     * ASSIGN_UUID: UUID（String 类型）
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 字段映射
     * value: 数据库字段名
     * exist: 是否为数据库字段
     */
    @TableField("user_name")
    private String username;
    
    /**
     * 不映射到数据库
     */
    @TableField(exist = false)
    private String tempField;
    
    /**
     * 自动填充
     * INSERT: 插入时填充
     * UPDATE: 更新时填充
     * INSERT_UPDATE: 插入和更新时填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    
    /**
     * 逻辑删除
     * 0: 未删除
     * 1: 已删除
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 乐观锁
     */
    @Version
    private Integer version;
}
```

### 2. Service 层设计

```java
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

// Service 接口
public interface UserService extends IService<User> {
    
    PageResult<User> pageQuery(UserQueryDTO dto, PageParam pageParam);
    
    User getByUsername(String username);
}

// Service 实现
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> 
        implements UserService {
    
    @Override
    public PageResult<User> pageQuery(UserQueryDTO dto, PageParam pageParam) {
        return baseMapper.selectPage(pageParam, new LambdaQueryWrapperX<User>()
            .likeIfPresent(User::getUsername, dto.getUsername())
            .eqIfPresent(User::getStatus, dto.getStatus())
            .betweenIfPresent(User::getCreateTime, dto.getStartTime(), dto.getEndTime())
            .orderByDesc(User::getCreateTime));
    }
    
    @Override
    public User getByUsername(String username) {
        return baseMapper.selectOne(User::getUsername, username);
    }
}
```


### 3. 复杂查询示例

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 多条件动态查询
     */
    public PageResult<Order> search(OrderSearchDTO dto, PageParam pageParam) {
        return orderMapper.selectPage(pageParam, new LambdaQueryWrapperX<Order>()
            // 订单号模糊查询
            .likeIfPresent(Order::getOrderNo, dto.getOrderNo())
            // 用户ID精确查询
            .eqIfPresent(Order::getUserId, dto.getUserId())
            // 订单状态IN查询
            .inIfPresent(Order::getStatus, dto.getStatusList())
            // 金额区间查询
            .betweenIfPresent(Order::getTotalAmount, dto.getMinAmount(), dto.getMaxAmount())
            // 时间范围查询
            .betweenIfPresent(Order::getCreateTime, dto.getStartTime(), dto.getEndTime())
            // 排序
            .orderByDesc(Order::getCreateTime));
    }
    
    /**
     * 关联查询（使用 XML）
     */
    public List<OrderVO> listWithDetails(Long userId) {
        return orderMapper.selectOrderWithDetails(userId);
    }
    
    /**
     * 统计查询
     */
    public OrderStatisticsVO statistics(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        // 使用 MyBatis-Plus 的聚合查询
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.select(
            "COUNT(*) as totalCount",
            "SUM(total_amount) as totalAmount",
            "AVG(total_amount) as avgAmount"
        );
        wrapper.eq("user_id", userId);
        wrapper.between("create_time", startTime, endTime);
        
        Map<String, Object> map = orderMapper.selectMaps(wrapper).get(0);
        
        OrderStatisticsVO vo = new OrderStatisticsVO();
        vo.setTotalCount(((Number) map.get("totalCount")).longValue());
        vo.setTotalAmount((BigDecimal) map.get("totalAmount"));
        vo.setAvgAmount((BigDecimal) map.get("avgAmount"));
        return vo;
    }
    
    /**
     * 批量操作
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Long> orderIds, Integer status) {
        // 方式1：使用 update 方法
        orderMapper.update(null, new LambdaUpdateWrapper<Order>()
            .set(Order::getStatus, status)
            .in(Order::getId, orderIds));
        
        // 方式2：批量更新（性能更好）
        List<Order> orders = orderIds.stream()
            .map(id -> {
                Order order = new Order();
                order.setId(id);
                order.setStatus(status);
                return order;
            })
            .collect(Collectors.toList());
        orderMapper.updateBatchById(orders, new LambdaUpdateWrapper<>());
    }
}
```


### 4. 多数据库适配示例

**Mapper.xml 示例：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mapper.OrderMapper">
    
    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
        id, order_no, user_id, total_amount, status, 
        create_time, update_time, deleted
    </sql>
    
    <!-- MySQL 分页查询 -->
    <select id="selectOrderPage" resultType="Order" databaseId="mysql">
        SELECT <include refid="Base_Column_List"/>
        FROM t_order
        WHERE deleted = 0
        <if test="userId != null">
            AND user_id = #{userId}
        </if>
        ORDER BY create_time DESC
        LIMIT #{offset}, #{limit}
    </select>
    
    <!-- Oracle 分页查询 -->
    <select id="selectOrderPage" resultType="Order" databaseId="oracle">
        SELECT * FROM (
            SELECT ROWNUM rn, t.* FROM (
                SELECT <include refid="Base_Column_List"/>
                FROM t_order
                WHERE deleted = 0
                <if test="userId != null">
                    AND user_id = #{userId}
                </if>
                ORDER BY create_time DESC
            ) t WHERE ROWNUM &lt;= #{offset} + #{limit}
        ) WHERE rn > #{offset}
    </select>
    
    <!-- PostgreSQL 分页查询 -->
    <select id="selectOrderPage" resultType="Order" databaseId="postgresql">
        SELECT <include refid="Base_Column_List"/>
        FROM t_order
        WHERE deleted = 0
        <if test="userId != null">
            AND user_id = #{userId}
        </if>
        ORDER BY create_time DESC
        LIMIT #{limit} OFFSET #{offset}
    </select>
    
    <!-- 达梦数据库分页查询 -->
    <select id="selectOrderPage" resultType="Order" databaseId="dm">
        SELECT <include refid="Base_Column_List"/>
        FROM t_order
        WHERE deleted = 0
        <if test="userId != null">
            AND user_id = #{userId}
        </if>
        ORDER BY create_time DESC
        LIMIT #{offset}, #{limit}
    </select>
    
    <!-- 字符串拼接 - MySQL -->
    <select id="searchByKeyword" resultType="Order" databaseId="mysql">
        SELECT <include refid="Base_Column_List"/>
        FROM t_order
        WHERE CONCAT(order_no, user_name) LIKE CONCAT('%', #{keyword}, '%')
    </select>
    
    <!-- 字符串拼接 - Oracle -->
    <select id="searchByKeyword" resultType="Order" databaseId="oracle">
        SELECT <include refid="Base_Column_List"/>
        FROM t_order
        WHERE order_no || user_name LIKE '%' || #{keyword} || '%'
    </select>
    
    <!-- 字符串拼接 - PostgreSQL/达梦 -->
    <select id="searchByKeyword" resultType="Order" databaseId="postgresql">
        SELECT <include refid="Base_Column_List"/>
        FROM t_order
        WHERE order_no || user_name LIKE '%' || #{keyword} || '%'
    </select>
    
</mapper>
```


## 常见问题

### 1. 为什么 JPA 提示不能使用？

JPA 提示需要根据 Mapper 找到实体类，有以下几种方式：

1. **继承 MyBatis-Plus 的 BaseMapper**（推荐）
   ```java
   public interface UserMapper extends BaseMapperX<User> {
   }
   ```

2. **Mapper.xml 文件有 resultMap 标签**
   ```xml
   <resultMap id="BaseResultMap" type="com.example.entity.User">
       <id column="id" property="id"/>
       <result column="username" property="username"/>
   </resultMap>
   ```

3. **在 Mapper 类上增加注释指定实体类**
   ```java
   /**
    * @Entity com.example.entity.User
    */
   public interface UserMapper extends BaseMapper<User> {
   }
   ```

### 2. 为什么生成的表名和期望的不一致？

JPA 提示生成代码按照以下规则找到表名：

1. **实体类有 JPA 注解**
   ```java
   @Table(name="t_user")
   public class User {
   }
   ```

2. **实体类有 MyBatis-Plus 注解**（推荐）
   ```java
   @TableName("t_user")
   public class User {
   }
   ```

3. **实体类有注释**
   ```java
   /**
    * @TableName t_user
    */
   public class User {
   }
   ```

4. **驼峰转下划线**（默认）
   - `UserModel` -> `user_model`
   - `SysUser` -> `sys_user`

### 3. 分页查询总数为 0？

检查以下几点：

1. **确认使用了 PageParam**
   ```java
   PageParam pageParam = new PageParam();
   pageParam.setPageNo(1);
   pageParam.setPageSize(10);
   ```

2. **确认使用了 selectPage 方法**
   ```java
   PageResult<User> result = userMapper.selectPage(pageParam, wrapper);
   ```

3. **检查是否配置了分页插件**（本组件已自动配置）

### 4. 批量插入不生效？

使用增强的批量插入方法：

```java
// 推荐：使用增强方法
userMapper.insertBatchSomeColumn(users);

// 或者：配置批量插入
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 添加批量插入插件
    return interceptor;
}
```

### 5. 逻辑删除不生效？

确认配置正确：

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted  # 全局逻辑删除字段
      logic-delete-value: 1        # 删除值
      logic-not-delete-value: 0    # 未删除值
```

实体类配置：

```java
@TableLogic
private Integer deleted;
```


### 6. 多数据源如何配置？

使用 MyBatis-Plus 的多数据源插件 `dynamic-datasource-spring-boot-starter`：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
    <version>3.5.1</version>
</dependency>
```

配置多数据源：

```yaml
spring:
  datasource:
    dynamic:
      primary: master  # 主数据源
      strict: false    # 严格模式
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/db1
          username: root
          password: password
          driver-class-name: com.mysql.cj.jdbc.Driver
        slave:
          url: jdbc:mysql://localhost:3306/db2
          username: root
          password: password
          driver-class-name: com.mysql.cj.jdbc.Driver
```

使用 `@DS` 注解切换数据源：

```java
@Service
public class UserService {
    
    @DS("master")  // 使用主库
    public void save(User user) {
        userMapper.insert(user);
    }
    
    @DS("slave")   // 使用从库
    public List<User> list() {
        return userMapper.selectList();
    }
}
```

### 7. 如何处理枚举类型？

**方式1：使用 MyBatis-Plus 的枚举处理器**

```java
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    NORMAL(0, "正常"),
    DISABLED(1, "禁用");
    
    @EnumValue  // 标记数据库存储值
    private final Integer code;
    
    @JsonValue  // 标记 JSON 序列化值
    private final String desc;
    
    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
```

配置枚举包扫描：

```yaml
mybatis-plus:
  type-enums-package: com.example.enums
```

**方式2：使用 MyBatis 的 TypeHandler**

```java
@MappedTypes(UserStatus.class)
public class UserStatusTypeHandler extends BaseTypeHandler<UserStatus> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, 
                                     UserStatus parameter, JdbcType jdbcType) 
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }
    
    @Override
    public UserStatus getNullableResult(ResultSet rs, String columnName) 
            throws SQLException {
        int code = rs.getInt(columnName);
        return UserStatus.fromCode(code);
    }
    
    // ... 其他方法
}
```


## 性能优化建议

### 1. 使用批量操作

```java
// ❌ 不推荐：循环插入
for (User user : users) {
    userMapper.insert(user);
}

// ✅ 推荐：批量插入
userMapper.insertBatchSomeColumn(users);
```

### 2. 合理使用索引

```sql
-- 为常用查询字段添加索引
CREATE INDEX idx_username ON sys_user(username);
CREATE INDEX idx_create_time ON sys_user(create_time);

-- 复合索引
CREATE INDEX idx_status_time ON sys_user(status, create_time);
```

### 3. 避免 SELECT *

```java
// ❌ 不推荐
List<User> users = userMapper.selectList(wrapper);

// ✅ 推荐：只查询需要的字段
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.select("id", "username", "email");
List<User> users = userMapper.selectList(wrapper);

// 或使用 Lambda 方式
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.select(User::getId, User::getUsername, User::getEmail);
List<User> users = userMapper.selectList(wrapper);
```

### 4. 分页查询优化

```java
// 大数据量分页优化：使用游标方式
public void processLargeData() {
    long pageNo = 1;
    long pageSize = 1000;
    
    while (true) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo((int) pageNo);
        pageParam.setPageSize((int) pageSize);
        
        PageResult<User> result = userMapper.selectPage(pageParam, 
            new LambdaQueryWrapper<User>()
                .orderByAsc(User::getId));
        
        if (result.getList().isEmpty()) {
            break;
        }
        
        // 处理数据
        processData(result.getList());
        
        pageNo++;
    }
}
```

### 5. 使用缓存

```java
// 开启二级缓存
@CacheNamespace(implementation = MybatisRedisCache.class)
public interface UserMapper extends BaseMapperX<User> {
}

// 或使用 Spring Cache
@Service
public class UserService {
    
    @Cacheable(value = "user", key = "#id")
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
    
    @CacheEvict(value = "user", key = "#user.id")
    public void update(User user) {
        userMapper.updateById(user);
    }
}
```


## 核心 API 速查表

### BaseMapperX 方法列表

| 方法 | 说明 | 示例 |
|------|------|------|
| `selectOne(String field, Object value)` | 根据字段查询单条 | `selectOne("username", "admin")` |
| `selectOne(SFunction field, Object value)` | Lambda 方式查询单条 | `selectOne(User::getUsername, "admin")` |
| `selectList()` | 查询所有 | `selectList()` |
| `selectList(String field, Object value)` | 根据字段查询列表 | `selectList("status", 1)` |
| `selectList(SFunction field, Object value)` | Lambda 方式查询列表 | `selectList(User::getStatus, 1)` |
| `selectList(String field, Collection values)` | IN 查询 | `selectList("id", Arrays.asList(1,2,3))` |
| `selectCount()` | 统计所有 | `selectCount()` |
| `selectCount(String field, Object value)` | 根据条件统计 | `selectCount("status", 1)` |
| `selectPage(PageParam, Wrapper)` | 分页查询 | `selectPage(pageParam, wrapper)` |
| `insertBatch(Collection)` | 批量插入 | `insertBatch(users)` |
| `insertBatchSomeColumn(Collection)` | 批量插入（增强） | `insertBatchSomeColumn(users)` |
| `updateBatch(T)` | 批量更新 | `updateBatch(user)` |
| `updateBatchById(Collection, Wrapper)` | 批量更新（指定条件） | `updateBatchById(users, wrapper)` |

### LambdaQueryWrapperX 方法列表

| 方法 | 说明 | 示例 |
|------|------|------|
| `eqIfPresent` | 等于（智能判空） | `eqIfPresent(User::getStatus, status)` |
| `neIfPresent` | 不等于（智能判空） | `neIfPresent(User::getStatus, status)` |
| `gtIfPresent` | 大于（智能判空） | `gtIfPresent(User::getAge, age)` |
| `geIfPresent` | 大于等于（智能判空） | `geIfPresent(User::getAge, age)` |
| `ltIfPresent` | 小于（智能判空） | `ltIfPresent(User::getAge, age)` |
| `leIfPresent` | 小于等于（智能判空） | `leIfPresent(User::getAge, age)` |
| `likeIfPresent` | 模糊查询（智能判空） | `likeIfPresent(User::getUsername, name)` |
| `inIfPresent` | IN 查询（智能判空） | `inIfPresent(User::getId, ids)` |
| `betweenIfPresent` | 区间查询（智能判空） | `betweenIfPresent(User::getAge, min, max)` |

### 配置项速查

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `sapling.mybatis.common-field-value-auto-fill-enable` | 是否开启字段自动填充 | false |
| `sapling.mybatis.base-package` | Mapper 扫描路径 | - |
| `mybatis-plus.configuration.map-underscore-to-camel-case` | 驼峰转换 | true |
| `mybatis-plus.global-config.db-config.id-type` | 主键策略 | - |
| `mybatis-plus.global-config.db-config.logic-delete-field` | 逻辑删除字段 | - |
| `mybatis-plus.mapper-locations` | Mapper XML 位置 | classpath*:/mapper/**/*.xml |


## 与 MyBatis-Plus 的对比

| 特性 | MyBatis-Plus | Sapling MyBatis Plus Starter |
|------|--------------|------------------------------|
| 基础 CRUD | ✅ | ✅ |
| 条件构造器 | ✅ | ✅ 增强（智能判空） |
| 分页查询 | ✅ | ✅ 封装 PageResult |
| 代码生成 | ✅ | ✅ 集成 MybatisX |
| 多数据库支持 | ✅ | ✅ 增强（国产数据库） |
| 数据库识别 | JDBC 驱动 | JDBC URL（更准确） |
| 便捷查询方法 | ❌ | ✅ BaseMapperX |
| 批量操作增强 | 部分 | ✅ 完整支持 |
| 字段自动填充 | ✅ | ✅ 可配置开关 |

## 版本兼容性

| 组件 | 版本要求 |
|------|----------|
| JDK | 1.8+ |
| Spring Boot | 2.x |
| MyBatis-Plus | 3.5.x |
| MyBatis | 3.5.x |

## 依赖说明

本组件依赖以下核心库：

- `mybatis-plus-boot-starter`: MyBatis-Plus 核心
- `mybatis-plus-generator`: 代码生成器
- `velocity-engine-core`: 模板引擎
- `hutool-all`: 工具类库

## 更新日志

### v1.0.0
- ✨ 初始版本发布
- ✨ 提供 BaseMapperX 增强 Mapper
- ✨ 提供 LambdaQueryWrapperX 和 QueryWrapperX
- ✨ 支持多数据库自动识别
- ✨ 封装分页查询 PageParam 和 PageResult
- ✨ 集成 MybatisX 代码生成

## 参考资料

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [MyBatis 官方文档](https://mybatis.org/mybatis-3/zh/index.html)
- [MybatisX 插件文档](https://baomidou.com/pages/ba5b24/)

## 技术支持

如有问题，请提交 Issue 或联系开发团队。

---

**Happy Coding! 🚀**
