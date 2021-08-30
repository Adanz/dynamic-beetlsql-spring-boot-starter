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
	
	@Bean
	public DynamicDatasourceConfigProvider dynamicDatasourceConfigProvider() {
		return new DynamicDatasourceConfigProvider() {
			@Override
			public Map<String, DataSourceProperty> getDataSourcePropertyMap(SQLManager sqlManager, String param) {
				Map<String, DataSourceProperty> map = new HashMap<>();

				List<Map<String, Object>> rows = new ArrayList<>();
				Map<String, Object> item = new HashMap<>();
				item.put("name", "ds3");
				item.put("username", "");
				item.put("password", "");
				item.put("url", "jdbc:mysql://localhost:3306/db3?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
				item.put("driverClassName", "com.mysql.cj.jdbc.Driver");
				rows.add(item);
				item = new HashMap<>();
				item.put("name", "ds4");
				item.put("username", "");
				item.put("password", "");
				item.put("url", "jdbc:mysql://localhost:3306/db4?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
				item.put("driverClassName", "com.mysql.cj.jdbc.Driver");
				rows.add(item);

				for (Map<String, Object> row: rows) {
					String name = row.get("name").toString();
					String username = row.get("username").toString();
					String password = row.get("password").toString();
					String url = row.get("url").toString();
					String driver = row.get("driverClassName").toString();
					DataSourceProperty property = new DataSourceProperty();
					property.setUsername(username);
					property.setPassword(password);
					property.setUrl(url);
					property.setDriverClassName(driver);
					map.put(name, property);
				}
				return map;
			}
		};
	}	
}
