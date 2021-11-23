package com.qeedata.data.tenant.context;

/**
 * 保存租户代码
 * @author adanz
 * @date 2020-09-31
 */
public class TenantCodeHolder {
    private static final ThreadLocal<String> TENANT_CODE_HOLDER = new InheritableThreadLocal<>();

    public static void setTenantCode(String tenantCode) {
        TENANT_CODE_HOLDER.set(tenantCode);
    }

    public static String getTenantCode() {
        return TENANT_CODE_HOLDER.get();
    }

    public static void clear() {
        TENANT_CODE_HOLDER.remove();
    }
}
