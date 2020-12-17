package com.qeedata.data.beetlsql.dynamic;

import lombok.Data;

/**
 * BeetlSql 配置属性
 * @author adanz
 * @since 2019.08.13
 */
@Data
public class BeetlSqlProperty {
  /**
   * 配置 beetlSql.daoSuffix来自动扫描com包极其子包下的所有以Dao结尾的Mapper类
   */
  private String basePackage = "com";
  /**
   * 通过类后缀 来自动注入Dao
   */
  private String daoSuffix = "Mapper";
  /**
   * 存放sql文件的根目录
   */
  private String sqlPath = "sql";
  /**
   * 数据库和javapojo的映射关系
   */
  private String nameConversion = "org.beetl.sql.core.UnderlinedNameConversion";
  /**
   * 何种数据库
   */
  private String dbStyle = "org.beetl.sql.core.db.MySqlStyle";
  /**
   * 是否输出debug
   */
  private Boolean dev = true;
  /**
   * ds
   */
  private String ds;

  /**
   * slave
   */
  private String slave;

  /**
   * dynamicCondition
   */
  private  String dynamicCondition;

  /**
   * dynamicSqlManager
   */
  private  String dynamicSqlManager;

  /**
   * dynamicSqlManager
   */
  private  String dynamicConnectionSource;
}
