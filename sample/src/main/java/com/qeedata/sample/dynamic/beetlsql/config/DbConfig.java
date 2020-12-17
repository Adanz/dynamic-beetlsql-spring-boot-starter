package com.qeedata.sample.dynamic.beetlsql.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.qeedata.data.beetlsql.dynamic.DynamicDataSourceTransactionManager;
import com.qeedata.data.beetlsql.dynamic.SqlManagerCustomize;
import org.beetl.sql.core.SQLManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

	/**
	 * 默认已生成 
	 */
	///@Primary
	///@Bean
	///public PlatformTransactionManager txDynamicManager(DataSource datasource) {
	///	DataSourceTransactionManager transactionManager = new DynamicDataSourceTransactionManager();
	///	transactionManager.setDataSource(datasource);
	///	return transactionManager;
	///}

	@Bean(name = "ds1TransactionManager")
	public PlatformTransactionManager txPrimaryManager(DataSource datasource) {
		DataSource ds = ((DynamicRoutingDataSource) datasource).getDataSource("ds1");
		return new DataSourceTransactionManager(ds);
	}

	@Bean(name = "ds2TransactionManager")
	public PlatformTransactionManager txSlaveManager(DataSource datasource) {
		DataSource ds = ((DynamicRoutingDataSource) datasource).getDataSource("ds2");
		return new DataSourceTransactionManager(ds);
	}

	@Bean
	public SqlManagerCustomize customize() {
		return new MySqlManagerCustomize();
	}

	public class MySqlManagerCustomize implements SqlManagerCustomize {
		@Override
		public void customize(String sqlManagerName , SQLManager manager) {
			System.out.println("SQLManager customize:" + sqlManagerName);
		}
	}
}
