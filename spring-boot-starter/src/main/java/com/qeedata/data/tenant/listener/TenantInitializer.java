package com.qeedata.data.tenant.listener;

import org.springframework.core.env.Environment;

/**
 * 租户数据源信息初始化器接口
 * @author adanz
 * @since 2020-09-31
 */
public interface TenantInitializer {
    void init(Environment environment);
}
