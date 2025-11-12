# Mybatis场景启动器快速开发指南

## 简介

sapling-mybatis-plus-boot-starter 基于MyBatis-Plus 3.5.1 进行封装。同样沿袭Mybatis-Plus的思想，**只做增强不做改变，为简化开发、提高效率而生**，使用方式与MyBatis-Plus无异。

## 支持数据库

> 任何能使用 `MyBatis` 进行 CRUD, 并且支持标准 SQL 的数据库，具体支持情况如下。

- MySQL，Oracle，DB2，H2，HSQL，SQLite，PostgreSQL，SQLServer，Phoenix，Gauss ，ClickHouse，Sybase，OceanBase，Firebird，Cubrid，Goldilocks，csiidb

- 达梦数据库，虚谷数据库，人大金仓数据库，南大通用(华库)数据库，南大通用数据库，神通数据库，瀚高数据库
## 增强
* 提供了基于jdbc-url识别数据库的[DatabaseIdProvider](#DatabaseIdProvider)
* 提供了基于`com.baomidou.mybatisplus.core.mapper.BaseMapper`的增强型Mapper`mapper.core.com.sapling.frame.mybatis.BaseMapperX`
   * 在 MyBatis Plus 的 BaseMapper 的基础上拓展，提供更多的能力,建议修改模板Mybatis-X模板实现此Mapper！模板如下，修改方法见[生成代码的模板配置](#gen)
  ```java
  package ${mapperInterface.packageName};
  
  import ${tableClass.fullClassName};
  <#if tableClass.pkFields??>
      <#list tableClass.pkFields as field><#assign pkName>${field.shortTypeName}</#assign></#list>
  </#if>
  
  /**
  * @author ${author!}
  * @description 针对表【${tableClass.tableName}<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】的数据库操作Mapper
  * @createDate ${.now?string('yyyy-MM-dd HH:mm:ss')}
  * @Entity ${tableClass.fullClassName}
  */
  public interface ${mapperInterface.fileName} extends BaseMapperX<${tableClass.shortClassName}> {
  
  }
  ```
* 提供了基于`com.baomidou.mybatisplus.core.conditions.query.QueryWrapper`的增强型QueryWrapper`query.core.com.sapling.frame.mybatis.QueryWrapperX`
   * 拼接条件的方法，增加 xxxIfPresent 方法，用于判断值不存在的时候，不要拼接到条件中。
* 提供了基于`com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper`的增强型LambdaQueryWrapper`query.core.com.sapling.frame.mybatis.LambdaQueryWrapperX`
   * 拼接条件的方法，增加 xxxIfPresent 方法，用于判断值不存在的时候，不要拼接到条件中。

## 快速开始

### 安装

```XML
<dependency>
   <groupId>com.sapling</groupId>
   <artifactId>sapling-mybatis-plus-boot-starter</artifactId>
   <version>${last_update_version}</version>
</dependency> 
```
### 配置

```properties
# 否开启默认字段填充，默认为false。开启后，会自动在执行 insert 和 update 语句时，自动插入或更新 createTime updateTime 字段
sapling.mybatis.common-field-value-auto-fill-enable=false
# 扫描mapper的根路径，同@MapperScan
sapling.mybatis.base-package=com.sapling.demo
spring.datasource.url=jdbc:mysql://localhost:3306/framework?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=****
```
## 开发流程
1. 使用代码生成工具直接生成Mapper及其Mapp.xml，建议配合[Mybatis-X](#Mybatis-X)使用。
2. 优先使用Mybatis-Plus提供的CRUD通用接口，及其条件构造器进行SQL语句组装
3. 无法通过步骤2进行适配的SQL语句，可以再XML中配合[databaseId](#databaseId)属性进行数据库语句适配
## <span id = "DatabaseIdProvider">数据库适配</span>

#### 实现方式

>  MyBatis 原DatabaseIdProvider是根据JDBC驱动包的数据库厂商ID来进行识别，部分国产数据库支持性不是很好。
>
> 本Starter采用Mybatis-Plus分页插件方案，使用**jdbc-url**进行判断。具体规则如下。

```java
    public static DbType getDbType(String jdbcUrl) {
        Assert.isFalse(StringUtils.isBlank(jdbcUrl), "Error: The jdbcUrl is Null, Cannot read database type");
        String url = jdbcUrl.toLowerCase();
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (url.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (url.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (url.contains(":sqlserver:") || url.contains(":microsoft:")) {
            return DbType.SQL_SERVER2005;
        } else if (url.contains(":sqlserver2012:")) {
            return DbType.SQL_SERVER;
        } else if (url.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (url.contains(":hsqldb:")) {
            return DbType.HSQL;
        } else if (url.contains(":db2:")) {
            return DbType.DB2;
        } else if (url.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (url.contains(":h2:")) {
            return DbType.H2;
        } else if (regexFind(":dm\\d*:", url)) {
            return DbType.DM;
        } else if (url.contains(":xugu:")) {
            return DbType.XU_GU;
        } else if (regexFind(":kingbase\\d*:", url)) {
            return DbType.KINGBASE_ES;
        } else if (url.contains(":phoenix:")) {
            return DbType.PHOENIX;
        } else if (jdbcUrl.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (jdbcUrl.contains(":gbase:")) {
            return DbType.GBASE;
        } else if (jdbcUrl.contains(":gbasedbt-sqli:")) {
            return DbType.GBASEDBT;
        } else if (jdbcUrl.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (jdbcUrl.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (jdbcUrl.contains(":sybase:")) {
            return DbType.SYBASE;
        } else if (jdbcUrl.contains(":oceanbase:")) {
            return DbType.OCEAN_BASE;
        } else if (url.contains(":highgo:")) {
            return DbType.HIGH_GO;
        } else if (url.contains(":cubrid:")) {
            return DbType.CUBRID;
        } else if (url.contains(":goldilocks:")) {
            return DbType.GOLDILOCKS;
        } else if (url.contains(":csiidb:")) {
            return DbType.CSIIDB;
        } else if (url.contains(":sap:")) {
            return DbType.SAP_HANA;
        } else if (url.contains(":impala:")) {
            return DbType.IMPALA;
        } else {
            logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DbType.OTHER;
        }
    }
   ```

#### <span id="databaseId">databaseId 字典</span>

| databaseId    | 数据库                                          |
| ------------- | ----------------------------------------------- |
| mysql         | MySql数据库                                     |
| mariadb       | MariaDB数据库                                   |
| oracle        | Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW) |
| oracle12c     | ORACLE_12C("oracle12c", "Oracle12c+数据库"),    |
| db2           | DB2数据库                                       |
| h2            | H2数据库                                        |
| hsql          | HSQL数据库                                      |
| sqlite        | SQLite数据库                                    |
| postgresql    | Postgre数据库                                   |
| sqlserver2005 | SQLServer2005数据库                             |
| sqlserver     | SQLServer数据库                                 |
| dm            | 达梦数据库                                      |
| xugu          | 虚谷数据库                                      |
| kingbasees    | 人大金仓数据库                                  |
| phoenix       | Phoenix HBase数据库                             |
| zenith        | Gauss 数据库                                    |
| clickhouse    | clickhouse 数据库                               |
| gbase         | 南大通用数据库                                  |
| oscar         | 神通数据库                                      |
| sybase        | Sybase ASE 数据库                               |
| oceanbase     | OceanBase 数据库                                |
| Firebird      | Firebird 数据库                                 |
| other         | 其他数据库                                      |



## TODO

- [ ] 主键策略 <https://baomidou.com/pages/e131bd/>



  ---



# <span id="Mybatis-X">MybatisX快速开发插件</span>

MybatisX 是一款基于 IDEA 的快速开发插件，为效率而生。

安装方法：打开 IDEA，进入 File -> Settings -> Plugins -> Browse Repositories，输入 `mybatisx` 搜索并安装。

## 功能

### XML 跳转

### ![跳转](.\img\mybatisx-jump.gif)



###  生成代码(需先在 idea 配置 Database 配置数据源)

![生成代码](.\img\mybatisx-generate.gif)

### 重置模板![生成代码](.\img\mybatisx-reset-template.gif)



### JPA 提示

生成新增
![生成新增](.\img\mybatisx-tip-insert.gif)

### 生成查询

### ![生成查询](.\img\mybatisx-tip-select.gif)

### 生成修改

### ![生成修改](.\img\mybatisx-tip-update.gif)

### 生成删除

### ![生成删除](.\img\mybatisx-tip-delete.gif)

## 常见问答

**为什么 JPA 不能使用?**
JPA 提示的方式需要根据 Mapper 找到实体类, 找到实体类有以下五种方式

1. 继承 mybatis-plus 的 BaseMapper
2. Mapper.xml 文件有 resultMap 标签
3. 在 Mapper 类上增加注释指定实体类, 例如: `@Entity com.xx.xx.UserModel`

**为什么生成的表名和期望的表名不一致**
JPA 提示生成代码, 按照以下规则找到表名

1. 实体类有 JPA 注解, 例如: `@Table(name="t_user")`
2. 实体类有 mybais-plus 注解, 例如: `@TableName("t_user")`
3. 实体类有注释: `@TableName com.xx.xx.UserModel`
4. 如果不存在以上规则, 将驼峰转下划线. 例如 UserMode 的表名为: user_model

# <span id="gen">生成代码的模板配置</span>

按照指定目录找到插件模板配置目录 Scratches and Consoles -> Extensions -> MybatisX
这里会提供默认模板: 例如在 1.4.13 提供了模板: `default-all`,`default`,`mybatis-plus2`,`mybatis-plus3`
如果想重置默认模板, 可以右键点击 MybatisX 目录,选择 `Restore Default Extensions` 选项

![代码生成模板配置](.\img\mybatisx-template-setting.jpg)

自定义模板内容

| 名称                      | 含义                       |
| ------------------------- | -------------------------- |
| tableClass.fullClassName  | 类的全称(包括包名)         |
| tableClass.shortClassName | 类的简称                   |
| tableClass.tableName      | 表名                       |
| tableClass.pkFields       | 表的所有主键字段           |
| tableClass.allFields      | 表的所有字段               |
| tableClass.baseFields     | 排除主键和 blob 的所有字段 |
| tableClass.baseBlobFields | 排除主键的所有字段         |
| tableClass.remark         | 表注释                     |

字段信息

| 名称                | 含义                              |
| ------------------- | --------------------------------- |
| field.fieldName     | 字段名称                          |
| field.columnName    | 列名称                            |
| field.jdbcType      | jdbc 类型                         |
| field.columnLength  | 列段长度                          |
| field.columnScale   | 列的精度                          |
| field.columnIsArray | 字段类型是不是数组类型            |
| field.shortTypeName | java 类型短名称, 通常用于定义字段 |
| field.fullTypeName  | java 类型的长名称, 通常用于导入   |
| field.remark        | 字段注释                          |
| field.autoIncrement | 是否自增                          |
| field.nullable      | 是否允许为空                      |

配置信息

| 名称                    | 含义                   |
| ----------------------- | ---------------------- |
| baseInfo.shortClassName | 配置名称               |
| baseInfo.tableName      | 配置文件名称           |
| baseInfo.pkFields       | 配置名称               |
| baseInfo.allFields      | 后缀                   |
| baseInfo.baseFields     | 包名                   |
| baseInfo.baseBlobFields | 模板内容               |
| baseInfo.remark         | 相对模块的资源文件路径 |