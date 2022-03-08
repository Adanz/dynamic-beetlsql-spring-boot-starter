package com.qeedata.data.tenant.processor;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.qeedata.data.tenant.context.TenantContext;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

/**
 * 根据当前租户和数据源组名称进行切换数据源
 * @author adanz
 * @since 2020-03-31
 */
public class DsTenantProcessor extends DsProcessor {

    /**
     * tenant ds prefix
     * 例: @DS("#tenantDS.slave")
     */
    private static final String HEADER_PREFIX = "#tenantDS";

    @Override
    public boolean matches(String key) {
        return key.startsWith(HEADER_PREFIX);
    }

    @Override
    public String doDetermineDatasource(MethodInvocation invocation, String key) {
        String s = key.substring(10);
        if (StringUtils.isEmpty(s)) {
            s = "master";
        }
        return TenantContext.getCurrentTenantDsName(s);
    }
}
