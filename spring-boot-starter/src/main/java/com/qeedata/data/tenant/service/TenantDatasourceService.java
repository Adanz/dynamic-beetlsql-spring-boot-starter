package com.qeedata.data.tenant.service;

import com.qeedata.data.tenant.entity.TenantDatasource;
import com.qeedata.data.tenant.service.TenantDatasourceService;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 租户数据源配置，保存在内存中
 * @author adanz
 * @date 2020-09-31
 */
public class TenantDatasourceService {
    private static final Set<TenantDatasource> TENANT_DATA_SOURCE_SET = new HashSet<>();

    public void add(TenantDatasource tenantDatasource) {
        TENANT_DATA_SOURCE_SET.add(tenantDatasource);
    }

    public void del(String tenantCode) {
        TENANT_DATA_SOURCE_SET.removeIf(source -> source.getTenantCode().equals(tenantCode));
    }

    public List<TenantDatasource> list() {
        return new ArrayList<>(TENANT_DATA_SOURCE_SET);
    }

    public String[] getTenantCodes() {
        Set<String> result = new HashSet<>();
        for (TenantDatasource source: TENANT_DATA_SOURCE_SET) {
            result.add(source.getTenantCode());
        }
        return result.toArray(new String[0]);
    }

    public String[] getGroupCodes() {
        Set<String> result = new HashSet<>();
        for (TenantDatasource source: TENANT_DATA_SOURCE_SET) {
            result.add(source.getGroupCode());
        }
        return result.toArray(new String[0]);
    }

    public String[] getDsNames() {
        Set<String> result = new HashSet<>();
        for (TenantDatasource source: TENANT_DATA_SOURCE_SET) {
            result.add(source.getDsName());
        }
        return result.toArray(new String[0]);
    }

    public String[] getDsNames(String... groupCodes) {
        Set<String> result = new HashSet<>();
        Set<String> groupSet = new HashSet<>(Arrays.asList(groupCodes));
        for (TenantDatasource source: TENANT_DATA_SOURCE_SET) {
            if (groupSet.contains(source.getGroupCode())) {
                result.add(source.getDsName());
            }
        }
        return result.toArray(new String[0]);
    }

    public String getTenantDsName(String tenantCode, String groupCode) {
        for (TenantDatasource source: TENANT_DATA_SOURCE_SET) {
            if (source.getTenantCode().equals(tenantCode) && source.getGroupCode().equals(groupCode)) {
                return source.getDsName();
            }
        }
        return null;
    }

    public String[] getTenantDsNames(String tenantCode, String... groupCodes) {
        List<String> dsNameList = new ArrayList<>();
        Set<String> groupSet = new HashSet<>(Arrays.asList(groupCodes));
        for (TenantDatasource source: TENANT_DATA_SOURCE_SET) {
            if (source.getTenantCode().equals(tenantCode) && groupSet.contains(source.getGroupCode())) {
                dsNameList.add(source.getDsName());
            }
        }
        if (dsNameList.size() == 0) {
            return null;
        }
        return dsNameList.toArray(new String[0]);
    }
}
