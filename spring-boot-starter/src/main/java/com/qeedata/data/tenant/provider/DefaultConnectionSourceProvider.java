package com.qeedata.data.tenant.provider;

import com.qeedata.data.beetlsql.dynamic.provider.DynamicConnectionSourceProvider;
import com.qeedata.data.tenant.context.TenantContext;

/**
 * 缺省实现的动态数据源提供者 (SQLManager 使用)
 * @author adanz
 * @since 2020-09-31
 */
public class DefaultConnectionSourceProvider implements DynamicConnectionSourceProvider {

    @Override
    public String[] getConnectionSources() {
        return TenantContext.getDsNames();
    }
}
