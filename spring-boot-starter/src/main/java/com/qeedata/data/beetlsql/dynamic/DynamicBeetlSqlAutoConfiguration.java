package com.qeedata.data.beetlsql.dynamic;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.spring.SpringConnectionSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动配置 BeetlSqlSource SqlManager Bean
 * @author adanz
 * @since 2020.12.03
 */
@Configuration
@AutoConfigureAfter({DynamicDataSourceAutoConfiguration.class})
@Import({BeetlSqlBeanRegister.class})
@ConditionalOnProperty(prefix = BeetlSqlConfig.PREFIX_BEETLSQL, name = {"enabled"}, havingValue = "true", matchIfMissing = true)
public class DynamicBeetlSqlAutoConfiguration {
	@Autowired(required=false)
	private SqlManagerCustomize cust;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private Environment env;

	@PostConstruct
	public void init() {
		DynamicBeetlSqlProperties properties = new BeetlSqlConfig(env).getProperties();
		Map<String, BeetlSqlProperty> beetlSqlPropertyMap = properties.getBeetlsql();

		for (Map.Entry<String, BeetlSqlProperty> item : beetlSqlPropertyMap.entrySet()) {
			String name = item.getKey();
			BeetlSqlProperty property = beetlSqlPropertyMap.get(name);
			if(property.getDynamicConnectionSource() != null) {
				setConditionalConnectionSource(name, property);
			}
		}

		if (cust != null) {
			for (String name : properties.getSqlManagers()) {
				SQLManager sqlManager = applicationContext.getBean(name,SQLManager.class);
				cust.customize(name,sqlManager);
			}
		}

	}

	/**
	 * 设置 ConditionalConnectionSource，同一 SqlManager 根据条件切换连接
	 */
	private void setConditionalConnectionSource(String name, BeetlSqlProperty property) {
		BeetlSqlBeanRegister beanRegister = new BeetlSqlBeanRegister();

		String[] connectionSources = property.getDynamicConnectionSource().split(",");
		Map<String, SpringConnectionSource> connectionSourceMap = new HashMap<>(16);

		for(String source : connectionSources){
			String beanName = source + "BeetlSqlDataSourceBean";
			beanRegister.registerBeetlSqlSourceBean(source, property);
			SpringConnectionSource cs = applicationContext.getBean(beanName, SpringConnectionSource.class);
			connectionSourceMap.put(source, cs);
		}

		// 配置策略
		ConditionalSpringConnectionSource.Policy policy = new ConditionalSpringConnectionSource.Policy() {
			final String defaultCsName = connectionSources[0];

			@Override
			public String getConnectionSourceName(ExecuteContext ctx, boolean isUpdate) {
				// 按当前数据源
				String csName = DynamicDataSourceContextHolder.peek();
				if (!StringUtils.isEmpty(csName)) {
					return csName;
				} else {
					return defaultCsName;
				}
			}

			@Override
			public String getMasterName() {
				return defaultCsName;
			}
		};

		ConditionalSpringConnectionSource cs  = new ConditionalSpringConnectionSource(policy, connectionSourceMap);

		SQLManager sqlManager = applicationContext.getBean(name, SQLManager.class);
		sqlManager.setDs(cs);
	}


	@Primary
	@Bean
	@ConditionalOnProperty(prefix = BeetlSqlConfig.PREFIX_BEETLSQL, name = "transactionManager", havingValue = "true", matchIfMissing = true)
	public PlatformTransactionManager txDynamicManager(DataSource datasource) {
		DataSourceTransactionManager transactionManager = new DynamicDataSourceTransactionManager();
		transactionManager.setDataSource(datasource);
		return transactionManager;
	}
}
