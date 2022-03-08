package com.qeedata.data.tenant.transaction;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.qeedata.data.tenant.context.TenantContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * DynamicMultiTenantTransactionManager
 * 事务处理中,通过 DynamicMultiTenantTransactionManager 使得
 * 同线程中获取的连接 Connection 或 DataSource一致
 *
 * @author adanz
 * @since 2022/01/10
 */
public class DynamicMultiTenantTransactionManager extends DataSourceTransactionManager {

    @Override
    protected DataSource obtainDataSource() {
        DataSource dataSource = this.getDataSource();
        Assert.state(dataSource != null, "No DataSource set");
        if (dataSource instanceof DynamicRoutingDataSource) {
            String dsName = DynamicDataSourceContextHolder.peek();
            if (StringUtils.isEmpty(dsName)) {
                dsName = TenantContext.getCurrentTenantDsName();
            }
            ///DataSource ds = ((DynamicRoutingDataSource) dataSource).determineDataSource();
            return ((DynamicRoutingDataSource) dataSource).getDataSource(dsName);
        } else {
            return dataSource;
        }
    }
}
