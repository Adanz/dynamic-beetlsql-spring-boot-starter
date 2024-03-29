package com.qeedata.data.beetlsql.dynamic.configure;

import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.qeedata.data.beetlsql.dynamic.*;
import com.qeedata.data.beetlsql.dynamic.ext.ConditionalSpringConnectionSource;
import com.qeedata.data.beetlsql.dynamic.transaction.DynamicDataSourceTransactionManager;
import com.qeedata.data.beetlsql.dynamic.group.DynamicConnectionSourceGroup;
import com.qeedata.data.beetlsql.dynamic.provider.DynamicConnectionSourceProvider;
import org.beetl.core.fun.ObjectUtil;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.spring.SpringConnectionSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
import java.util.*;

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
	private DataSource dataSource;

	@Autowired
	private DefaultDataSourceCreator dataSourceCreator;

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
			if(property.getDynamicConnectionSource() != null ||
				property.getDynamicConnectionSourceProvider() != null) {
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

	private  ClassLoader  getClassLoader(){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null) {
			classLoader = this.getClass().getClassLoader();
		}
		return classLoader;
	}

	/**
	 * 设置 ConditionalConnectionSource，同一 SqlManager 根据条件切换连接
	 */
	private void setConditionalConnectionSource(String name, BeetlSqlProperty property) {
		BeetlSqlBeanRegister beanRegister = new BeetlSqlBeanRegister();

		String[] connectionSources = new String[0];
		// 如果是 dynamicConnectionSourceProvider
		// 用于如从数据表中定义数据源的场景
		if (property.getDynamicConnectionSourceProvider() != null && property.getDynamicConnectionSourceGroup() != null) {
			String[] groupCodes = property.getDynamicConnectionSourceGroup().split(",");
			String dynamicDatasourceProvider = property.getDynamicConnectionSourceProvider();
			Object instance = ObjectUtil.tryInstance(dynamicDatasourceProvider, getClassLoader());
			try {
				DynamicConnectionSourceGroup connectionSourceGroup = (DynamicConnectionSourceGroup) instance;
				connectionSourceGroup.setGroupCodes(groupCodes);
				DynamicConnectionSourceProvider provider = (DynamicConnectionSourceProvider) instance;
				connectionSources = provider.getConnectionSources();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (property.getDynamicConnectionSourceProvider() != null) {
			String dynamicConnectionSourceProvider = property.getDynamicConnectionSourceProvider();
			try {
				DynamicConnectionSourceProvider provider = (DynamicConnectionSourceProvider) ObjectUtil.tryInstance(dynamicConnectionSourceProvider, getClassLoader());
				connectionSources = provider.getConnectionSources();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			connectionSources = property.getDynamicConnectionSource().split(",");
		}

		Map<String, SpringConnectionSource> connectionSourceMap = new HashMap<>(16);

		if (connectionSources == null) {
			return;
		}

		for(String source : connectionSources){
			String beanName = source + "BeetlSqlDataSourceBean";
			beanRegister.registerBeetlSqlSourceBean(source, property.getSlave());
			SpringConnectionSource cs = applicationContext.getBean(beanName, SpringConnectionSource.class);
			connectionSourceMap.put(source, cs);
		}

		// 配置策略
		ConditionalSpringConnectionSource.Policy policy = null;
		if (property.getDynamicConnectionPolicy() != null && property.getDynamicConnectionSourceGroup() != null) {
			String[] groupCodes = property.getDynamicConnectionSourceGroup().split(",");
			String dynamicConnectionPolicy = property.getDynamicConnectionPolicy();
			Object instance = ObjectUtil.tryInstance(dynamicConnectionPolicy, getClassLoader());
			try {
				DynamicConnectionSourceGroup connectionSourceGroup = (DynamicConnectionSourceGroup) instance;
				connectionSourceGroup.setGroupCodes(groupCodes);
				policy = (ConditionalSpringConnectionSource.Policy) instance;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (property.getDynamicConnectionPolicy() != null) {
			String dynamicConnectionPolicy = property.getDynamicConnectionPolicy();
			try {
				policy = (ConditionalSpringConnectionSource.Policy) ObjectUtil.tryInstance(dynamicConnectionPolicy, getClassLoader());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String[] finalConnectionSources = connectionSources;
			policy = new ConditionalSpringConnectionSource.Policy() {
				final String defaultCsName = finalConnectionSources[0];

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
		}

		ConditionalSpringConnectionSource cs  = new ConditionalSpringConnectionSource(policy, connectionSourceMap);

		SQLManager sqlManager = applicationContext.getBean(name, SQLManager.class);
		sqlManager.setDs(cs);
	}


	@Primary
	@Bean
	@ConditionalOnMissingBean
	public PlatformTransactionManager txDynamicManager(DataSource datasource) {
		DataSourceTransactionManager transactionManager = new DynamicDataSourceTransactionManager();
		transactionManager.setDataSource(datasource);
		return transactionManager;
	}
}
