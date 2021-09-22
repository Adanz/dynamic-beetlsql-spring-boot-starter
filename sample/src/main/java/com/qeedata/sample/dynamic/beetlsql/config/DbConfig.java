package com.qeedata.sample.dynamic.beetlsql.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.qeedata.data.beetlsql.dynamic.SqlManagerCustomize;
import org.beetl.sql.core.SQLManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DbConfig {

//	@Primary
//	@Bean
//	@ConditionalOnMissingBean
//	public PlatformTransactionManager txDynamicManager(DataSource datasource) {
//		DataSourceTransactionManager transactionManager = new DynamicDataSourceTransactionManager();
//		transactionManager.setDataSource(datasource);
//		return transactionManager;
//	}

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
		return new SqlManagerCustomize() {
			@Override
			public void customize(String sqlManagerName, SQLManager manager) {
				System.out.println("SQLManager customize:" + sqlManagerName);
			}
		};
	}

	/**
	 * 加载动态数据源
	 * @return DynamicDataSourceProvider
	 */
	@Bean
	public DynamicDataSourceProvider dynamicDataSourceProvider() {
		return new AbstractDataSourceProvider() {
			@Override
			public Map<String, DataSource> loadDataSources() {
				Map<String, DataSourceProperty> map = new HashMap<>();

				List<Map<String, Object>> rows = new ArrayList<>();
				Map<String, Object> item = new HashMap<>();
                item.put("name", "ds3");
                item.put("username", "");
                item.put("password", "");
                item.put("url", "jdbc:mysql://localhost:3306/demo1?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
                item.put("driverClassName", "com.mysql.cj.jdbc.Driver");
                rows.add(item);
                item = new HashMap<>();
                item.put("name", "ds4");
                item.put("username", "");
                item.put("password", "");
                item.put("url", "jdbc:mysql://localhost:3306/demo2?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
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
				return createDataSourceMap(map);
			}
		};
	}
}
