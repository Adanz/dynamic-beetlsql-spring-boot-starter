package com.qeedata.sample.dynamic.beetlsql.config;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.qeedata.data.beetlsql.dynamic.ext.ConditionalSpringConnectionSource;
import org.beetl.sql.core.ExecuteContext;
import org.springframework.util.StringUtils;

public class DemoConnectionSourcePolicy implements ConditionalSpringConnectionSource.Policy{
    final String defaultCsName = "ds1";

    @Override
    public String getConnectionSourceName(ExecuteContext ctx, boolean isUpdate) {
        // 按当前数据源
        String csName = DynamicDataSourceContextHolder.peek();
        if (!StringUtils.isEmpty(csName)) {
            return csName;
        } else {
            return defaultCsName;
        }
    }

    @Override
    public String getMasterName() {
        return defaultCsName;
    }
}
