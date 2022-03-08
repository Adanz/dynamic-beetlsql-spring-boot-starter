package com.qeedata.data.tenant.entity;

import lombok.Data;

/**
 * 分租户数据源配置
 *  tenantCode - 租户代码
 *  groupCode - 分组代码，相当于，同类业务库为同一组
 *  dsName - 数据名称
 * @author adanz
 * @since 2020-09-31
 */
@Data
public class TenantDatasource {
    private String tenantCode;
    private String groupCode;
    private String dsName;
}
