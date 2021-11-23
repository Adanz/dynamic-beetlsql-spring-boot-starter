package com.qeedata.data.tenant.provider;

import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.qeedata.data.tenant.context.TenantContext;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 缺省实现的租户数据源提供者(Spring-dynamin-datasource 使用)
 * @author adanz
 * @date 2020-09-31
 */
public class DefaultDataSourceProvider extends AbstractDataSourceProvider {
    @Override
    public Map<String, DataSource> loadDataSources() {
        return createDataSourceMap(TenantContext.getDataSourcePropertyMap());
    }
}
