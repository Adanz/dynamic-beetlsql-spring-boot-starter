package com.qeedata.data.tenant.context;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.qeedata.data.tenant.entity.DatasourceConfig;
import com.qeedata.data.tenant.entity.TenantDatasource;
import com.qeedata.data.tenant.service.DatasourceConfigService;
import com.qeedata.data.tenant.service.TenantDatasourceService;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 租户信息上下文
 * @author adanz
 * @date 2020-09-31
 */
public class TenantContext {
    public enum BalancingType {
        SIMPLE, // 取第一个
        POLLING // 轮询
    }
    private static final DatasourceConfigService datasourceConfigService = new DatasourceConfigService();
    private static final TenantDatasourceService tenantDatasourceService = new TenantDatasourceService();
    private static final AtomicInteger index = new AtomicInteger(0);

    // 缺省 group code
    private static String defaultGroupCode = "master";
    private static Boolean enable = true;

    private static BalancingType balancingType = BalancingType.SIMPLE;

    public static BalancingType getBalancingType() {
        return balancingType;
    }

    public static void setBalancingType(BalancingType balancingType) {
        TenantContext.balancingType = balancingType;
    }

    public static Boolean isEnable() {
        return enable;
    }

    public static void setEnable(Boolean enable) {
        TenantContext.enable = enable;
    }


    public static String getDefaultGroupCode() {
        return defaultGroupCode;
    }

    public static void setDefaultGroupCode(String defaultGroupCode) {
        TenantContext.defaultGroupCode = defaultGroupCode;
    }

    public static void addDatasourceConfing(DatasourceConfig config) {
        datasourceConfigService.add(config);
    }

    public static Map<String, DataSourceProperty> getDataSourcePropertyMap() {
        return datasourceConfigService.getDataSourcePropertyMap();
    }

    public static void addTenantDatasource(TenantDatasource tenantDatasource) {
        tenantDatasourceService.add(tenantDatasource);
    }

    public static String[] getTenantCodes() {
        return tenantDatasourceService.getTenantCodes();
    }

    public static String[] getGroupCodes() {
        return tenantDatasourceService.getGroupCodes();
    }

    public static String[] getDsNames() {
        return tenantDatasourceService.getDsNames();
    }

    public static String[] getDsNames(String... groupCodes) {
        return tenantDatasourceService.getDsNames(groupCodes);
    }

    public static String getTenantDsName(String tenantCode, String groupCode) {
        return tenantDatasourceService.getTenantDsName(tenantCode, groupCode);
    }

    public static String getTenantDsName(String tenantCode) {
        return tenantDatasourceService.getTenantDsName(tenantCode, defaultGroupCode);
    }

    public static String[] getTenantDsNames(String tenantCode, String... groupCodes) {
        return tenantDatasourceService.getTenantDsNames(tenantCode, groupCodes);
    }

    public static String getTenantDsName(String tenantCode, String... groupCodes) {
        if (balancingType == BalancingType.SIMPLE) {
            return getTenantDsName(tenantCode, groupCodes[0]);
        } else if (balancingType == BalancingType.POLLING) {
            String[] dsNames =  getTenantDsNames(tenantCode, groupCodes);
            return dsNames[Math.abs(index.getAndAdd(1) % dsNames.length)];
        } else {
            return null;
        }
    }


    public static String getCurrentTenantDsName(String... groupCodes) {
        String tenantCode = TenantCodeHolder.getTenantCode();
        return getTenantDsName(tenantCode, groupCodes);
    }

    public static String getCurrentTenantDsName(String groupCode) {
        String tenantCode = TenantCodeHolder.getTenantCode();
        return getTenantDsName(tenantCode, groupCode);
    }

    public static String getCurrentTenantDsName() {
        String tenantCode = TenantCodeHolder.getTenantCode();
        return getTenantDsName(tenantCode, defaultGroupCode);
    }

    public static void setCurrentTenantDs(String groupCode) {
        String dsName = getCurrentTenantDsName(groupCode);
        DynamicDataSourceContextHolder.push(dsName);
    }

    public static void setCurrentTenantDs() {
        String dsName = getCurrentTenantDsName();
        DynamicDataSourceContextHolder.push(dsName);
    }

}
