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

## 简介

dynamic-beetlsql-spring-boot-starter 的主要功能是整合 [dynamic-datasource-spring-boot-starter](https://gitee.com/baomidou/dynamic-datasource-spring-boot-starter) 和 [BeetlSQL](https://gitee.com/xiandafu/beetlsql), 方便配置和使用多数据源。

支持 **Jdk 1.8+,SpringBoot 2.x.x, dynamic-datasource-spring-boot-starter 3.4.1, beetlSQL 3.10.0-RELEASE**。

## 特性

1. 支持 dynamic-datasource-spring-boot-starter 多数据源配置。
2. 支持 dynamic-datasource-spring-boot-starter 通过注解切换多数源。 
3. 支持 BeetlSQL 多个 SqlManager 配置。
4. 支持 BeetlSQL ConditionalSqlManager 配置。
5. 支持 BeetlSQL ConditionalConnectionSource 配置。
6. 提供开箱即用的多租户支持（独立数据源/数据库模式）。

## [使用文档](http://qeedata.com/open/dynamic-beetlsql/introduction/)

## 最后更新
### v1.3.0 (2022-01-21)
1. 增加多租户支持。
---