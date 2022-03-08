package com.qeedata.data.tenant.entity;

import lombok.Data;

/**
 * 数据源配置
 * @author adanz
 * @since 2020-09-31
 */
@Data
public class DatasourceConfig {
    private String name;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
