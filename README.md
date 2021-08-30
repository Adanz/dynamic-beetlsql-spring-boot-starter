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

支持 **Jdk 1.8+,SpringBoot 2.x.x, dynamic-datasource-spring-boot-starter 3.4.1, beetlSQL 3.7.0-RELEASE**。

# 特性

1. 支持 dynamic-datasource-spring-boot-starter 多数据源配置。
2. 支持 dynamic-datasource-spring-boot-starter 通过注解切换多数源。 
3. 支持 BeetlSQL 多个 SqlManager 配置。
4. 支持 BeetlSQL ConditionalSqlManager 配置。
5. 支持 BeetlSQL ConditionalConnectionSource 配置。

# ChangeLog
### v1.0.0
1. 增加 dynamicDatasourceProvider 方式，数据源配置可由数据库表中定义
### v0.9.x
1. 整合 BeetlSQL 和 dynamic-datasource-spring-boot-starter 多数据源配置。
2. 增加 dynamic-datasource-spring-boot-starter 通过注解切换多数源。
3. 增加 BeetlSQL 多种配置方式，如多个 SqlManager 配置，ConditionalSqlManager，ConditionalConnectionSource 配置。

# 使用方法

### 1. 引入dynamic-beetlsql-spring-boot-starter。

```xml
<dependency>
  <groupId>com.qeedata</groupId>
  <artifactId>dynamic-beetlsql-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```
### 2. 配置数据源。

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

### 3. 配置 BeetlSQL。

有几种配置方式，以适应不同的应用场景：

#### A. 单独 sqlManager 模式
```properties
dynamic.beetlsql.sm.ds = ds1
dynamic.beetlsql.sm.sqlPath = sql
dynamic.beetlsql.sm.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.sm.daoSuffix = Dao
dynamic.beetlsql.sm.basePackage = com.qeedata.sample
dynamic.beetlsql.sm.dbStyle = org.beetl.sql.core.db.MySqlStyle
```
#### B. dynamicSqlManager 模式

使用同一个 SqlManager 来切换多个不同的 SqlManager,需先配置好多个 SqlManager

使用 BeetlSql 的 ConditionalSqlManager
```properties
dynamic.beetlsql.sm1.xxx = xxx
...
dynamic.beetlsql.sm2.xxx = xxx

dynamic.beetlsql.sm.sqlPath = sql
dynamic.beetlsql.sm.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.sm.daoSuffix = Dao
dynamic.beetlsql.sm.basePackage = com.qeedata.sample
dynamic.beetlsql.sm.dbStyle = org.beetl.sql.core.db.MySqlStyle
dynamic.beetlsql.sm.dynamicSqlManager = sm1,sm2
```
#### C. dynamicConnectionSource 模式

使用一个 sqlManager 切换多个不同的 Datasource,需先配置好多个 Datasource

使用 BeetlSql 的 ConditionalConnectionSource
```properties
spring.datasource.dynamic.datasource.ds1.xxx = xxx
...
spring.datasource.dynamic.datasource.ds2.xxx = xxx
...
spring.datasource.dynamic.datasource.ds3.xxx = xxx

dynamic.beetlsql.sm.sqlPath = sql
dynamic.beetlsql.sm.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.sm.daoSuffix = Dao
dynamic.beetlsql.sm.basePackage = com.qeedata.sample
dynamic.beetlsql.sm.dbStyle = org.beetl.sql.core.db.MySqlStyle
dynamic.beetlsql.sm.dynamicConnectionSource = ds1,ds2,ds3
```
#### D. dynamicDatasourceProvider 模式

使用一个 sqlManager 切换多个不同的 Datasource,
同 dynamicConnectionSource，不过多个 Datasource 配置通过实现 DynamicDatasourceConfigProvider 提供，
通常用于动态数据源由数据库表中定义，然后由 dynamicDatasourceProvider 指定的 SqlManager 来获取，参照 sample.

使用 BeetlSql 的 ConditionalConnectionSource
```properties
dynamic.beetlsql.smMaster.xxx = xxx

dynamic.beetlsql.smComm.sqlPath = sql
dynamic.beetlsql.smComm.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.smComm.daoSuffix = Dao
dynamic.beetlsql.smComm.basePackage = com.qeedata.sample
dynamic.beetlsql.smComm.dbStyle = org.beetl.sql.core.db.MySqlStyle
dynamic.beetlsql.smComm.dynamicDatasourceProvider = smMaster
```

java bean
```java
@Bean
public DynamicDatasourceConfigProvider dynamicDatasourceConfigProvider() {
    return new DynamicDatasourceConfigProvider() {
        @Override
        public Map<String, DataSourceProperty> getDataSourcePropertyMap(SQLManager sqlManager, String param) {
            Map<String, DataSourceProperty> map = new HashMap<>();

            // 测试
            List<Map<String, Object>> rows = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("name", "ds1");
            item.put("username", "");
            item.put("password", "");
            item.put("url", "jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
            item.put("driverClassName", "com.mysql.cj.jdbc.Driver");
            rows.add(item);
            
            // 实际应用中可由数据库获取
            // List<Map> rows = sqlManager.execute(new SQLReady("SELECT name, username, password, url, driverClassName FROM t_datasource"), Map.class);

            for (Map<String, Object> row: rows) {
                String name = row.get("name").toString();
                String username = row.get("username").toString();
                String password = row.get("password").toString();
                String url = row.get("url").toString();
                String driver = row.get("driverClassName").toString();
                DataSourceProperty property = new DataSourceProperty();
                property.setUsername(username);
                property.setPassword(password);
                property.setUrl(url);
                property.setDriverClassName(driver);
                map.put(name, property);
            }
            return map;
        }
    };
}
```
#### 示例
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

# 2, dynamicSqlManager 模式(可选) 
# 使用一个 sqlManager 切换, 使用 ConditionalSqlManager
dynamic.beetlsql.sm.sqlPath = sql
dynamic.beetlsql.sm.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.sm.daoSuffix = Dao
dynamic.beetlsql.sm.basePackage = com.qeedata.sample
dynamic.beetlsql.sm.dbStyle = org.beetl.sql.core.db.MySqlStyle
# 必须 dynamicSqlManager 表示使用 ConditionalSqlManager 模式
dynamic.beetlsql.sm.dynamicSqlManager = ds1,ds2

# 3, dynamicConnectionSource 模式(可选) 
# 使用一个 sqlManager 切换, 使用 ConditionalConnectionSource
dynamic.beetlsql.cs.sqlPath = sql
dynamic.beetlsql.cs.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.cs.daoSuffix = Dao
dynamic.beetlsql.cs.basePackage = com.qeedata.sample
dynamic.beetlsql.cs.dbStyle = org.beetl.sql.core.db.MySqlStyle
# 必须 dynamicConnectionSource 表示使用 ConditionalConnectionSource 模式
dynamic.beetlsql.cs.dynamicConnectionSource = ds1,ds2

# 4, dynamicDatasourceProvider 模式(可选) 
# 使用一个 sqlManager 切换, 使用 ConditionalConnectionSource
dynamic.beetlsql.master.sqlPath = sql
...

dynamic.beetlsql.cs.sqlPath = sql
dynamic.beetlsql.cs.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.cs.daoSuffix = Dao
dynamic.beetlsql.cs.basePackage = com.qeedata.sample
dynamic.beetlsql.cs.dbStyle = org.beetl.sql.core.db.MySqlStyle
# 必须 dynamicDatasourceProvider 表示使用 dynamicDatasourceProvider 模式
dynamic.beetlsql.cs.dynamicDatasourceProvider = master
```
### 4. 代码中使用

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

### 5. 多租户配置(独立DB方式)
推荐使用 dynamicConnectionSource 或 dynamicDatasourceProvider 模式，配合 dynamic-datasource 的 注解，如：
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
# 必须 dynamicConnectionSource 表示使用 dynamicConnectionSource 模式
dynamic.beetlsql.cs.dynamicConnectionSource = tenant1,tenant2
# 或由 dynamicDatasourceProvider 表示使用 dynamicDatasourceProvider 模式
# dynamic.beetlsql.cs.dynamicDatasourceProvider = master
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

### 6. 其它参阅 sample 。

---
