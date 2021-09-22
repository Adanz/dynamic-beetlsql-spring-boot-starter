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

支持 **Jdk 1.8+,SpringBoot 2.x.x, dynamic-datasource-spring-boot-starter 3.4.1, beetlSQL 3.10.0-RELEASE**。

# 特性

1. 支持 dynamic-datasource-spring-boot-starter 多数据源配置。
2. 支持 dynamic-datasource-spring-boot-starter 通过注解切换多数源。 
3. 支持 BeetlSQL 多个 SqlManager 配置。
4. 支持 BeetlSQL ConditionalSqlManager 配置。
5. 支持 BeetlSQL ConditionalConnectionSource 配置。

# ChangeLog
### v1.2.1 (2021-09-20)
1. Bug Fix。
### v1.2.0 (2021-09-16)
1. _(Breaking changes)_ 改名 ~~dynamicDatasourceProvider~~ 为 dynamicConnectionSourceProvider 指定具体类中实现。
### v1.1.0 (2021-09-15)
1. 增加 dynamicConnectionPolicy 可指定实现类，自定义切换 ConditionalConnectionSource 的策略
### v1.0.0 (2021-08-27)
1. 增加 ~~dynamicDatasourceProvider~~ 方式，数据源配置可由数据库表中定义
### v0.9.x (2020-12-12)
1. 整合 BeetlSQL 和 dynamic-datasource-spring-boot-starter 多数据源配置。
2. 增加 dynamic-datasource-spring-boot-starter 通过注解切换多数源。
3. 增加 BeetlSQL 多种配置方式，如多个 SqlManager 配置，ConditionalSqlManager，ConditionalConnectionSource 配置。

# 使用方法

### 1. 引入dynamic-beetlsql-spring-boot-starter。

```xml
<dependency>
  <groupId>com.qeedata</groupId>
  <artifactId>dynamic-beetlsql-spring-boot-starter</artifactId>
  <version>1.2.1</version>
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
dynamic.beetlsql.sm.dynamicConnectionPolicy = com.qeedata.sample.dynamic.beetlsql.config.DemoConnectionSourcePolicy
```
#### D. dynamicConnectionSourceProvider 模式

使用一个 sqlManager 切换多个不同的 Datasource,
同 dynamicConnectionSource，不过多个 Datasource 配置通过实现 DynamicDatasourceConfigProvider 提供，
通常用于动态数据源由数据库表中定义，然后由 dynamicConnectionSourceProvider 指定的类来获取，参照 sample.
动态连接的的切换策略一般默认即可，不需指定。特殊情况下，可自定义实现(dynamicConnectionPolicy)，如下：

使用 BeetlSql 的 ConditionalConnectionSource
```properties
dynamic.beetlsql.smMaster.xxx = xxx

dynamic.beetlsql.smComm.sqlPath = sql
dynamic.beetlsql.smComm.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.smComm.daoSuffix = Dao
dynamic.beetlsql.smComm.basePackage = com.qeedata.sample
dynamic.beetlsql.smComm.dbStyle = org.beetl.sql.core.db.MySqlStyle
dynamic.beetlsql.smComm.dynamicConnectionSourceProvider = com.qeedata.sample.dynamic.beetlsql.config.DemoConnectionSourceProvider
#dynamic.beetlsql.smComm.dynamicConnectionPolicy = com.qeedata.sample.dynamic.beetlsql.config.DemoConnectionSourcePolicy
```

###### 动态增加数据源 (dynamic-datasource-spring-boot-starter 提供的功能)
```java
@Bean
public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new AbstractDataSourceProvider() {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSourceProperty> map = new HashMap<>();
        
                List<Map<String, Object>> rows = new ArrayList<>();
                Map<String, Object> item = new HashMap<>();
                item.put("name", "ds3");
                item.put("username", "");
                item.put("password", "");
                item.put("url", "jdbc:mysql://localhost:3306/demo1?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
                item.put("driverClassName", "com.mysql.cj.jdbc.Driver");
                rows.add(item);
                item = new HashMap<>();
                item.put("name", "ds4");
                item.put("username", "");
                item.put("password", "");
                item.put("url", "jdbc:mysql://localhost:3306/demo2?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
                item.put("driverClassName", "com.mysql.cj.jdbc.Driver");
                rows.add(item);
    
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
                return createDataSourceMap(map);
            }
        };
}
```

###### DemoConnectionSourceProvider.java
```java
public class DemoConnectionSourceProvider implements DynamicConnectionSourceProvider {
    @Override
    public String[] getConnectionSources() {
        String[] sources = new String[]{"ds3", "ds4"};
        return sources;
    }
}
```
###### DemoConnectionSourcePolicy.java
```java
public class DemoConnectionSourcePolicy implements ConditionalSpringConnectionSource.Policy{
    final String defaultCsName = "ds1";

    @Override
    public String getConnectionSourceName(ExecuteContext ctx, boolean isUpdate) {
        // 按当前数据源
        String csName = DynamicDataSourceContextHolder.peek();
        if (!StringUtils.isEmpty(csName)) {
            return csName;
        } else {
            return defaultCsName;
        }
    }

    @Override
    public String getMasterName() {
        return defaultCsName;
    }
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

# 4, dynamicConnectionSourceProvider 模式(可选) 
# 使用一个 sqlManager 切换, 使用 ConditionalConnectionSource
dynamic.beetlsql.master.sqlPath = sql
...

dynamic.beetlsql.cs.sqlPath = sql
dynamic.beetlsql.cs.nameConversion = org.beetl.sql.core.JPA2NameConversion
dynamic.beetlsql.cs.daoSuffix = Dao
dynamic.beetlsql.cs.basePackage = com.qeedata.sample
dynamic.beetlsql.cs.dbStyle = org.beetl.sql.core.db.MySqlStyle
# 必须 dynamicConnectionSourceProvider 表示使用 dynamicConnectionSourceProvider 模式
dynamic.beetlsql.cs.dynamicConnectionSourceProvider = com.qeedata.sample.dynamic.beetlsql.config.DemoConnectionSourceProvider
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
推荐使用 dynamicConnectionSource 或 dynamicConnectionSourceProvider 模式，配合 dynamic-datasource 的 注解，如：
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
# 或由 dynamicConnectionSourceProvider 表示使用 dynamicConnectionSourceProvider 模式
# dynamic.beetlsql.cs.dynamicConnectionSourceProvider = com.qeedata.sample.dynamic.beetlsql.config.DemoConnectionSourceProvider
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
