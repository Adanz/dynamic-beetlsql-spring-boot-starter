package com.qeedata.data.tenant.listener;


import com.qeedata.data.beetlsql.dynamic.configure.BeetlSqlConfig;
import com.qeedata.data.beetlsql.dynamic.configure.DynamicBeetlSqlProperties;
import com.qeedata.data.tenant.context.TenantContext;
import com.qeedata.data.tenant.entity.TenantDatasource;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

/**
 * 租户数据源信息初始化
 * @author adanz
 * @since 2020-09-31
 */
@Configuration
public class TenantInitListener implements ApplicationListener<ApplicationContextInitializedEvent>, Ordered {
    public final static String MULTI_TENANT_PREFIX = "dynamic.multi-tenant";
    public final static String MULTI_TENANT_GROUP_PREFIX = MULTI_TENANT_PREFIX +  ".group";
    public final static String MULTI_TENANT_INIT_CLASS = MULTI_TENANT_PREFIX + ".init-class";
    public final static String MULTI_TENANT_ENABLE = MULTI_TENANT_PREFIX + ".enable";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent applicationContextInitializedEvent) {

        ConfigurableEnvironment environment = applicationContextInitializedEvent.getApplicationContext().getEnvironment();
        String initClassName = environment.getProperty(MULTI_TENANT_INIT_CLASS, "");
        boolean enable = Boolean.parseBoolean(environment.getProperty(MULTI_TENANT_ENABLE, "false"));

        TenantContext.setEnable(enable);

        if (enable) {
            if (!StringUtils.isEmpty(initClassName)) {
                try {
                    Class<?> clazz = Class.forName(initClassName);
                    TenantInitializer initializer = (TenantInitializer) clazz.newInstance();
                    initializer.init(environment);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            this.initDefaultTenantDatasource(environment);
        }
    }

    private void initDefaultTenantDatasource(ConfigurableEnvironment environment) {
        final String prefix = MULTI_TENANT_GROUP_PREFIX + ".%s.%s";
        DynamicBeetlSqlProperties properties = new BeetlSqlConfig(environment).getProperties();
        properties.getBeetlsql().forEach((sqlManager, property) -> {
            String groupCode = property.getDynamicConnectionSourceGroup();
            if (!StringUtils.isEmpty(groupCode)) {
                String ds = environment.getProperty(String.format(prefix, groupCode, "ds"), "");
                String tenant = environment.getProperty(String.format(prefix, groupCode, ".tenant"), "");
                if (!StringUtils.isEmpty(ds) && !StringUtils.isEmpty(tenant)) {
                    String[] dsNames = ds.split(",");
                    String[] tenantCodes = tenant.split(",");
                    for (int i = 0; i < dsNames.length; i++) {
                        String dsName = dsNames[i];
                        String tenantCode = tenantCodes[i];
                        TenantDatasource tenantDs = new TenantDatasource();
                        tenantDs.setGroupCode(groupCode);
                        tenantDs.setDsName(dsName);
                        tenantDs.setTenantCode(tenantCode);
                        TenantContext.addTenantDatasource(tenantDs);
                    }
                }
            }
        });
    }
}
