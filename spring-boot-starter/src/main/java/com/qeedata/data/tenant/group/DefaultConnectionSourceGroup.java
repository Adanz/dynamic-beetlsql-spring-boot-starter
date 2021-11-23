package com.qeedata.data.tenant.group;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.qeedata.data.beetlsql.dynamic.ext.ConditionalSpringConnectionSource;
import com.qeedata.data.beetlsql.dynamic.group.DynamicConnectionSourceGroup;
import com.qeedata.data.beetlsql.dynamic.provider.DynamicConnectionSourceProvider;
import com.qeedata.data.tenant.context.TenantContext;
import org.beetl.sql.core.ExecuteContext;
import org.springframework.util.StringUtils;

/**
 * 缺省实现的同组数据源中切换策略
 *  DynamicConnectionSourceProvider, ConditionalSpringConnectionSource, DynamicConnectionSourceGroup
 *
 * @author adanz
 * @date 2020-09-31
 */
public class DefaultConnectionSourceGroup
        implements DynamicConnectionSourceGroup, DynamicConnectionSourceProvider, ConditionalSpringConnectionSource.Policy {

    protected String[] groupCodes = new String[] {"master"};

    @Override
    public void setGroupCodes(String... groupCodes) {
        this.groupCodes = groupCodes;
    }

    @Override
    public String[] getConnectionSources() {
        return TenantContext.getDsNames(groupCodes);
    }

    @Override
    public String getConnectionSourceName(ExecuteContext ctx, boolean isUpdate) {
        String csName = DynamicDataSourceContextHolder.peek();
        if (!StringUtils.isEmpty(csName)) {
            return csName;
        } else {
            csName = TenantContext.getCurrentTenantDsName(groupCodes);
            if (!StringUtils.isEmpty(csName)) {
                return csName;
            }
            return null;
        }
    }
}
