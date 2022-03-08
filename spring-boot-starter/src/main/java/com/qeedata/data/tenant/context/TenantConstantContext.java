package com.qeedata.data.tenant.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 分租户保存系统常量
 * @author adanz
 * @since 2020-09-31
 */
public class TenantConstantContext {
    private static final Map<String, Map<String, Object>> CONSTANT_HOLDER = new HashMap<>();

    public static void putConstants(Map<String, Object> constantMap) {
        putConstants(getTenantCode(), constantMap);
    }

    public static void putConstant(String code, Object value) {
        putConstant(getTenantCode(), code, value);
    }

    private static void putConstants(String tenantCode, Map<String, Object> constantMap) {
        CONSTANT_HOLDER.computeIfAbsent(tenantCode, k -> new HashMap<>(16));
        constantMap.forEach(CONSTANT_HOLDER.get(tenantCode)::put);
    }

    private static void putConstant(String tenantCode, String code, Object value) {
        CONSTANT_HOLDER.computeIfAbsent(tenantCode, k -> new HashMap<>(16));
        CONSTANT_HOLDER.get(tenantCode).put(code, value);
    }

    public static Object getConstant(String code) {
        String tenantCode = getTenantCode();
        if (CONSTANT_HOLDER.get(tenantCode) != null) {
            return CONSTANT_HOLDER.get(tenantCode).get(code);
        }
        return null;
    }

    private static String getTenantCode() {
        return TenantCodeHolder.getTenantCode();
    }
}
