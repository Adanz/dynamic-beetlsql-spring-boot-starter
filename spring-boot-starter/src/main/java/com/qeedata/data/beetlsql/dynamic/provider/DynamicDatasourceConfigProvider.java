package com.qeedata.data.beetlsql.dynamic.provider;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import org.beetl.sql.core.SQLManager;

import java.util.Map;

public interface DynamicDatasourceConfigProvider {
    /**
     * 获取数据源
     *
     * @return 数据源列表，key为数据源名称
     */
    Map<String, DataSourceProperty> getDataSourcePropertyMap(SQLManager sqlManager, String param);

}
