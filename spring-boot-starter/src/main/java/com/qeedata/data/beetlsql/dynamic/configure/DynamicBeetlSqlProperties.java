package com.qeedata.data.beetlsql.dynamic.configure;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dynamic Beetlsql 属性
 * @author adanz
 * @since 2020-12-03
 */
@Data
public class DynamicBeetlSqlProperties {
    /**
     * sqlManager 列表
     */
    private List<String> sqlManagers = new ArrayList<>();

    /**
     * Primary sqlManager
     */
    private String primary;

    /**
     * 每一个BeetlSql 属性配置
     */
    private Map<String, BeetlSqlProperty> beetlsql = new LinkedHashMap<>();

    /**
     * BeetlSql 全局参数配置
     */
    private BeetlSqlProperty defaultBeetlsql = new BeetlSqlProperty();
}
