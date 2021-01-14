<p align="center">
	<strong>Dynamic beetlSQL spring boot starter</strong>
</p>

<p align="center">
    <a href="http://mvnrepository.com/artifact/com.qeedata/dynamic-beetlsql-spring-boot-starter" target="_blank">
        <img src="https://img.shields.io/maven-central/v/com.qeedata/dynamic-beetlsql-spring-boot-starter.svg" >
    </a>
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="http://img.shields.io/:license-apache-brightgreen.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/springBoot-2.x.x-green.svg" >
    </a>
</p>

# 简介

dynamic-beetlsql-spring-boot-starter 的主要功能是整合 [dynamic-datasource-spring-boot-starter](https://gitee.com/baomidou/dynamic-datasource-spring-boot-starter) 和 [BeetlSQL](https://gitee.com/xiandafu/beetlsql), 方便配置和使用多数据源。

支持 **Jdk 1.8+,SpringBoot 2.x.x, dynamic-datasource-spring-boot-starter 3.3.1, beetlSQL 3.2.6**。

# 特性

1. 支持 dynamic-datasource-spring-boot-starter 多数据源配置。
2. 支持 dynamic-datasource-spring-boot-starter 通过注解切换多数源。 
3. 支持 BeetlSQL 多个 SqlManager 配置。
4. 支持 BeetlSQL ConditionalSqlManager 配置。
5. 支持 BeetlSQL ConditionalConnectionSource 配置。


# 使用方法

1. 引入dynamic-beetlsql-spring-boot-starter。

```xml
<dependency>
  <groupId>com.qeedata</groupId>
  <artifactId>dynamic-beetlsql-spring-boot-starter</artifactId>
  <version>0.9.7</version>
</dependency>
```
2. 配置数据源。

```properties
# 参照并按 dynamic-datasource-spring-boot-starter 规则
# 设置默认的数据源或者数据源组,默认值即为 ds1
spring.datasource.dynamic.primary = ds1
spring.datasource.dynamic.p6spy = false
spring.datasource.dynamic.strict = false

# ds1
spring.datasource.dynamic.datasource.ds1.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.ds1.url=jdbc:mysql://localhost:3306/db1?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true
spring.datasource.dynamic.datasource.ds1.username=user
spring.datasource.dynamic.datasource.ds1.password=password

# ds2
spring.datasource.dynamic.datasource.ds2.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.ds2.url=jdbc:mysql://localhost:3306/db2?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true
spring.datasource.dynamic.datasource.ds2.username=user
spring.datasource.dynamic.datasource.ds2.password=password
```

3. 配置 BeetlSQL。

约定前缀为 dynamic.beetlsql

```properties
# sqlManagers = 多个 sqlManager 列表
dynamic.beetlsql.sqlManagers = ds1,ds2,sm,cs
# primary 默认 sqlManager
dynamic.beetlsql.primary = cs
# transactionManager 默认自动生成，如需自己管理则设置为 false
# dynamic.beetlsql.transactionManager = false

# 默认值 _default
dynamic.beetlsql._default.dev = true

# 1, 配置单独 sqlManager
# A) 名称: ds1, dataSource 同名称 ds1, 或 dynamic.beetlsql.ds1.ds = ds1
dynamic.beetlsql.ds1.sqlPath = sql
dynamic.beetlsql.ds1.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.ds1.daoSuffix = Dao
dynamic.beetlsql.ds1.basePackage = com.qeedata.sample
dynamic.beetlsql.ds1.dbStyle = org.beetl.sql.core.db.MySqlStyle
dynamic.beetlsql.ds1.slave = ds2

# B) 名称: ds2, dataSource 同名称 ds2, 或 dynamic.beetlsql.ds2.ds = ds2
dynamic.beetlsql.ds2.sqlPath = sql
dynamic.beetlsql.ds2.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.ds2.daoSuffix = Dao
dynamic.beetlsql.ds2.basePackage = com.qeedata.sample
dynamic.beetlsql.ds2.dbStyle = org.beetl.sql.core.db.MySqlStyle

# 2, ConditionalSqlManager 模式(可选) 
# 使用一个 sqlManager 切换, 使用 ConditionalSqlManager
dynamic.beetlsql.sm.sqlPath = sql
dynamic.beetlsql.sm.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.sm.daoSuffix = Dao
dynamic.beetlsql.sm.basePackage = com.qeedata.sample
dynamic.beetlsql.sm.dbStyle = org.beetl.sql.core.db.MySqlStyle
# 必须 dynamicSqlManager 表示使用 ConditionalSqlManager 模式
dynamic.beetlsql.sm.dynamicSqlManager = ds1,ds2

# 3, ConditionalConnectionSource 模式(可选) 
# 使用一个 sqlManager 切换, 使用 ConditionalConnectionSource
dynamic.beetlsql.cs.sqlPath = sql
dynamic.beetlsql.cs.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.cs.daoSuffix = Dao
dynamic.beetlsql.cs.basePackage = com.qeedata.sample
dynamic.beetlsql.cs.dbStyle = org.beetl.sql.core.db.MySqlStyle
# 必须 dynamicConnectionSource 表示使用 ConditionalConnectionSource 模式
dynamic.beetlsql.cs.dynamicConnectionSource = ds1,ds2
```
4. 代码中使用

```java
    @Autowired
    private SQLManager csManager;

    @DS("ds2")
    public List getUserList() {
        SqlId sqlId = SqlId.of("demo", "getUser");
        List<?> rows = csManager.select(sqlId, Map.class, null);
        return rows;
    }
```

5. 多租户配置(独立DB方式)
推荐使用 ConditionalConnectionSource 模式，配合 dynamic-datasource 的 注解，如：
(以下约定 tenantId 同 Datasource 名称)

```properties
spring.datasource.dynamic.primary = tenant1
spring.datasource.dynamic.p6spy = false
spring.datasource.dynamic.strict = false

# tenant1
spring.datasource.dynamic.datasource.tenant1.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.tenant1.url=jdbc:mysql://localhost:3306/db1
spring.datasource.dynamic.datasource.tenant1.username=user
spring.datasource.dynamic.datasource.tenant1.password=password

# tenant2
spring.datasource.dynamic.datasource.tenant2.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.dynamic.datasource.tenant2.url=jdbc:mysql://localhost:3306/db2
spring.datasource.dynamic.datasource.tenant2.username=user
spring.datasource.dynamic.datasource.tenant2.password=password

dynamic.beetlsql.sqlManagers = cs
dynamic.beetlsql.primary = cs

dynamic.beetlsql._default.dev = true

dynamic.beetlsql.cs.sqlPath = sql
dynamic.beetlsql.cs.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.cs.daoSuffix = Dao
dynamic.beetlsql.cs.basePackage = com.qeedata.sample
dynamic.beetlsql.cs.dbStyle = org.beetl.sql.core.db.MySqlStyle
# 必须 dynamicConnectionSource 表示使用 ConditionalConnectionSource 模式
dynamic.beetlsql.cs.dynamicConnectionSource = tenant1,tenant2
```

Java 代码：
```java
    @Autowired
    private SQLManager csManager;

    @DS("#header.tenantId")
    public List getUserList() {
        SqlId sqlId = SqlId.of("demo", "getUser");
        List<?> rows = csManager.select(sqlId, Map.class, null);
        return rows;
    }
```

6. 其它参阅 sample 。

# TODO
1. 运行中动态新增/修改/删除 datasource, SqlManager
2. 其它完善...

---
