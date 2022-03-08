package com.qeedata.sample.tenant.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.qeedata.data.tenant.context.TenantContext;
import com.qeedata.data.tenant.entity.DatasourceConfig;
import com.qeedata.data.tenant.entity.TenantDatasource;
import com.qeedata.data.tenant.listener.TenantInitializer;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class TenantInit implements TenantInitializer {

    @Override
    public void init(Environment environment) {
        final String configDatasource = "spring.datasource.dynamic.datasource.config";

        String driverClassName;
        String jdbcUrl;
        String username;
        String password;
        String datasoureName;

        Connection conn = null;
        try {
            // 数据源信息
            /**
            driverClassName = environment.getProperty(String.format("%s.driverClassName", configDatasource), "");
            jdbcUrl = environment.getProperty(String.format("%s.url", configDatasource), "");
            username = environment.getProperty(String.format("%s.username", configDatasource), "");
            password = environment.getProperty(String.format("%s.password", configDatasource), "");


            Class.forName(driverClassName);
            assert jdbcUrl != null;
            conn = DriverManager.getConnection(jdbcUrl, username, password);

            String sql = "SELECT name, username, password, url, driver_class_name as driverClassName FROM datasource ";
            List<Entity> entityList = SqlExecutor.query(conn, sql, new EntityListHandler());

            if (ObjectUtil.isNotEmpty(entityList)) {
                entityList.forEach(entity -> {
                            DatasourceConfig config = new DatasourceConfig();
                            config.setName(entity.getStr("name"));
                            config.setDriverClassName(entity.getStr("driverClassName"));
                            config.setUrl(entity.getStr("url"));
                            config.setUsername(entity.getStr("username"));
                            config.setPassword(entity.getStr("password"));
                            TenantContext.addDatasourceConfing(config);
                        }
                );
            }
             **/

            // 测试用
            datasoureName = "orderDb_ds1";
            driverClassName = "net.sourceforge.jtds.jdbc.Driver";
            jdbcUrl = "jdbc:jtds:sqlserver://172.20.32.73:1433;DatabaseName=NewRetData";
            username = "edpc";
            password = "EDP,2015";

            DatasourceConfig config = new DatasourceConfig();
            config.setName(datasoureName);
            config.setDriverClassName(driverClassName);
            config.setUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            TenantContext.addDatasourceConfing(config);

            datasoureName = "orderDb_ds2";
            driverClassName = "net.sourceforge.jtds.jdbc.Driver";
            jdbcUrl = "jdbc:jtds:sqlserver://172.20.32.73:1433;DatabaseName=NewRetData";
            username = "edpc";
            password = "EDP,2015";

            config = new DatasourceConfig();
            config.setName(datasoureName);
            config.setDriverClassName(driverClassName);
            config.setUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            TenantContext.addDatasourceConfing(config);

            datasoureName = "stockDb_ds1";
            driverClassName = "net.sourceforge.jtds.jdbc.Driver";
            jdbcUrl = "jdbc:jtds:sqlserver://172.20.32.73:1433;DatabaseName=NewRetData";
            username = "edpc";
            password = "EDP,2015";

            config = new DatasourceConfig();
            config.setName(datasoureName);
            config.setDriverClassName(driverClassName);
            config.setUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            TenantContext.addDatasourceConfing(config);

            datasoureName = "stockDb_ds2";
            driverClassName = "net.sourceforge.jtds.jdbc.Driver";
            jdbcUrl = "jdbc:jtds:sqlserver://172.20.32.73:1433;DatabaseName=NewRetData";
            username = "edpc";
            password = "EDP,2015";

            config = new DatasourceConfig();
            config.setName(datasoureName);
            config.setDriverClassName(driverClassName);
            config.setUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);
            TenantContext.addDatasourceConfing(config);


            // 租户信息
            /**
            sql = "SELECT tenant_code AS tenantCode, ds_group_code as groupCode, ds_code AS dsName from tenant_datasource";
            entityList = SqlExecutor.query(conn, sql, new EntityListHandler());

            if (ObjectUtil.isNotEmpty(entityList)) {
                entityList.forEach(entity -> {
                            TenantDatasource tenant = new TenantDatasource();
                            tenant.setTenantCode(entity.getStr("tenantCode"));
                            tenant.setGroupCode(entity.getStr("groupCode"));
                            tenant.setDsName(entity.getStr("dsName"));
                            TenantContext.addTenantDatasource(tenant);
                        }
                );
            }
             **/

            // 测试用
            String tenantCode = "tenant1";
            String groupCode = "order";
            String dsName = "orderDb_ds1";

            TenantDatasource tenant = new TenantDatasource();
            tenant.setTenantCode(tenantCode);
            tenant.setGroupCode(groupCode);
            tenant.setDsName(dsName);
            TenantContext.addTenantDatasource(tenant);

            tenantCode = "tenant2";
            groupCode = "order";
            dsName = "orderDb_ds2";

            tenant = new TenantDatasource();
            tenant.setTenantCode(tenantCode);
            tenant.setGroupCode(groupCode);
            tenant.setDsName(dsName);
            TenantContext.addTenantDatasource(tenant);

            tenantCode = "tenant1";
            groupCode = "stock";
            dsName = "stockDb_ds1";

            tenant = new TenantDatasource();
            tenant.setTenantCode(tenantCode);
            tenant.setGroupCode(groupCode);
            tenant.setDsName(dsName);
            TenantContext.addTenantDatasource(tenant);

            tenantCode = "tenant2";
            groupCode = "stock";
            dsName = "stockDb_ds2";

            tenant = new TenantDatasource();
            tenant.setTenantCode(tenantCode);
            tenant.setGroupCode(groupCode);
            tenant.setDsName(dsName);
            TenantContext.addTenantDatasource(tenant);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtil.close(conn);
        }

    }
}