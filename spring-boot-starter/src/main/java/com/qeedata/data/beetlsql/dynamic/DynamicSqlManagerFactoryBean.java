package com.qeedata.data.beetlsql.dynamic;

import org.beetl.core.fun.ObjectUtil;
import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * DynamicSqlManagerFactoryBean, 参考官方代码
 * @author adanz
 * @since 2020-12-03
 */
public class DynamicSqlManagerFactoryBean
		implements FactoryBean<SQLManager>, InitializingBean, ApplicationListener<ApplicationEvent>, ApplicationContextAware
{
	protected ConditionalSQLManager conditionalSQLManager = null;

	protected String  conditional = null;
	protected String defaultSQLManager = null;
	protected List<String> all = new ArrayList<>();
	protected ApplicationContext applicationContext;
	protected String name;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
	}

	@Override
	public SQLManager getObject() throws Exception {
		if (conditionalSQLManager != null) {
			return conditionalSQLManager;
		}

		SQLManager mainSQLManager = applicationContext.getBean(defaultSQLManager, SQLManager.class);
		HashMap<String, SQLManager> allManager = new HashMap<>(16);
		for(String sqlManagerName : all){
			SQLManager sqlManager = applicationContext.getBean(sqlManagerName, SQLManager.class);
			allManager.put(sqlManagerName,sqlManager);
		}
		ConditionalSQLManager sqlManager = new DynamicConditionalSqlManager(mainSQLManager,allManager);
		if(conditional != null) {

			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if(loader==null){
				loader = DynamicSqlManagerFactoryBean.class.getClassLoader();
			}
			ConditionalSQLManager.Conditional conditionalIns = (ConditionalSQLManager.Conditional)
					ObjectUtil.tryInstance(conditional,loader);
			sqlManager.setConditional(conditionalIns);
		}

		sqlManager.setName(name);
		sqlManager.register();
		conditionalSQLManager = sqlManager;

		return conditionalSQLManager;
	}

	@Override
	public Class<?> getObjectType() {
		return SQLManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public String getConditional() {
		return conditional;
	}

	public void setConditional(String conditional) {
		this.conditional = conditional;
	}

	public String getDefaultSQLManager() {
		return defaultSQLManager;
	}

	public void setDefaultSQLManager(String defaultSQLManager) {
		this.defaultSQLManager = defaultSQLManager;
	}

	public List<String> getAll() {
		return all;
	}

	public void setAll(List<String> all) {
		this.all = all;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
