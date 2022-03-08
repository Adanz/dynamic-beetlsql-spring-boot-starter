package com.qeedata.data.tenant.configuration;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.qeedata.data.tenant.listener.TenantInitListener;
import com.qeedata.data.tenant.provider.DefaultDataSourceProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置 spring-dynamic-datasource 数据源
 * @author adanz
 * @since 2021-09-10
 */
@Configuration
@ConditionalOnProperty(prefix = TenantInitListener.MULTI_TENANT_PREFIX, name = {"enable"}, havingValue = "true", matchIfMissing = false)
public class DynamicDatasourceConfig {
    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        return new DefaultDataSourceProvider();
    }
}
