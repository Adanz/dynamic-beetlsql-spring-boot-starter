package com.qeedata.data.tenant.listener;


import com.qeedata.data.tenant.context.TenantContext;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

/**
 * 租户数据源信息初始化
 * @author adanz
 * @date 2020-09-31
 */
@Configuration
public class TenantInitListener implements ApplicationListener<ApplicationContextInitializedEvent>, Ordered {
    public final static String MULTI_TENANT_PREFIX = "dynamic.multiTenant";
    public final static String MULTI_TENANT_INIT_CLASS = MULTI_TENANT_PREFIX + ".initClass";
    public final static String MULTI_TENANT_ENABLE = MULTI_TENANT_PREFIX + ".enable";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent applicationContextInitializedEvent) {

        ConfigurableEnvironment environment = applicationContextInitializedEvent.getApplicationContext().getEnvironment();
        String initClassName = environment.getProperty(MULTI_TENANT_INIT_CLASS, "");
        boolean enable = Boolean.parseBoolean(environment.getProperty(MULTI_TENANT_ENABLE, "true"));

        TenantContext.setEnable(enable);

        if (enable && !StringUtils.isEmpty(initClassName)) {
            try {
                Class<?> clazz = Class.forName(initClassName);
                TenantInitializer initializer = (TenantInitializer) clazz.newInstance();
                initializer.init(environment);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
