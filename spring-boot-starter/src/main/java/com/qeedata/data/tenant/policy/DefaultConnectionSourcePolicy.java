package com.qeedata.data.tenant.policy;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.qeedata.data.beetlsql.dynamic.ext.ConditionalSpringConnectionSource;
import com.qeedata.data.tenant.context.TenantContext;
import org.beetl.sql.core.ExecuteContext;
import org.springframework.util.StringUtils;

/**
 * 缺省实现的租户数据源切换策略
 * @author adanz
 * @since 2020-09-31
 */
public class DefaultConnectionSourcePolicy implements ConditionalSpringConnectionSource.Policy {
    @Override
    public String getConnectionSourceName(ExecuteContext ctx, boolean isUpdate) {
        // 按当前数据源
        String csName = DynamicDataSourceContextHolder.peek();
        if (!StringUtils.isEmpty(csName)) {
            return csName;
        } else {
            csName = TenantContext.getCurrentTenantDsName();
            if (!StringUtils.isEmpty(csName)) {
                return csName;
            }
            return null;
        }
    }
}