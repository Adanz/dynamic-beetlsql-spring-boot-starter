package com.qeedata.data.beetlsql.dynamic.ext;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * DynamicDataSourceTransactionManager
  * 事务处理中,通过 DynamicDataSourceTransactionManager 使得
 * 同线程中获取的连接 Connection 或 DataSource一致
 *
 * @author adanz
 * @since 2019/10/10
 */
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {

    @Override
    protected DataSource obtainDataSource() {
        DataSource dataSource = this.getDataSource();
        Assert.state(dataSource != null, "No DataSource set");
        if (dataSource instanceof DynamicRoutingDataSource) {
            ///DataSource ds = ((DynamicRoutingDataSource) dataSource).determineDataSource();
            return ((DynamicRoutingDataSource) dataSource).determineDataSource();
        } else {
            return dataSource;
        }
    }
}
