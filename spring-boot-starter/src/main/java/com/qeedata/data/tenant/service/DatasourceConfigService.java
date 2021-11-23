package com.qeedata.data.tenant.service;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.qeedata.data.tenant.entity.DatasourceConfig;

import java.util.*;

/**
 * 数据源配置，保存在内存中
 * @author adanz
 * @date 2020-09-31
 */
public class DatasourceConfigService {
    private static final Set<DatasourceConfig> DATA_SOURCE_CONF_SET = new HashSet<>();

    public void add(DatasourceConfig conf) {
        DATA_SOURCE_CONF_SET.add(conf);
    }

    public void del(String name) {
        DATA_SOURCE_CONF_SET.removeIf(config -> config.getName().equals(name));
    }

    public List<DatasourceConfig> list() {
        return new ArrayList<>(DATA_SOURCE_CONF_SET);
    }

    public Map<String, DataSourceProperty> getDataSourcePropertyMap() {
        Map<String, DataSourceProperty> propertyMap = new HashMap<>();
        for (DatasourceConfig conf: DATA_SOURCE_CONF_SET) {
            DataSourceProperty property = new DataSourceProperty();
            property.setDriverClassName(conf.getDriverClassName());
            property.setUrl(conf.getUrl());
            property.setUsername(conf.getUsername());
            property.setPassword(conf.getPassword());
            propertyMap.put(conf.getName(), property);
        }
        return propertyMap;
    }
}
