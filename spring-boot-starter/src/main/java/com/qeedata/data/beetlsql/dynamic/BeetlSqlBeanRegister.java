package com.qeedata.data.beetlsql.dynamic;

import com.qeedata.data.beetlsql.dynamic.configure.BeetlSqlConfig;
import com.qeedata.data.beetlsql.dynamic.configure.BeetlSqlProperty;
import com.qeedata.data.beetlsql.dynamic.configure.DynamicBeetlSqlProperties;
import com.qeedata.data.beetlsql.dynamic.ext.ConnectionSourceFactory;
import com.qeedata.data.beetlsql.dynamic.ext.DynamicSqlManagerFactoryBean;
import com.qeedata.data.beetlsql.dynamic.group.DynamicConnectionSourceGroup;
import com.qeedata.data.beetlsql.dynamic.provider.DynamicConnectionSourceProvider;
import org.beetl.core.fun.ObjectUtil;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring.BeetlSqlClassPathScanner;
import org.beetl.sql.ext.spring.SqlManagerFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

/**
 * @author xiandafu ,waote, adanz
 */
public class BeetlSqlBeanRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
	private static ResourceLoader resourceLoader;
	private static BeanDefinitionRegistry registry;
	private static Environment env;

	private DynamicBeetlSqlProperties beetlSqlProperties;
	private Map<String, BeetlSqlProperty> beetlSqlPropertyMap;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		BeetlSqlBeanRegister.registry = registry;

		beetlSqlProperties = new BeetlSqlConfig(env).getProperties();
		beetlSqlPropertyMap = beetlSqlProperties.getBeetlsql();
		readySqlManager();
	}

	@Override
	public void setResourceLoader(ResourceLoader loader) {
		BeetlSqlBeanRegister.resourceLoader = loader;
	}

	@Override
	public void setEnvironment(Environment env) {
		BeetlSqlBeanRegister.env = env;
	}

	public void registerBeetlSqlSourceBean(String source, String slaveSource) {
		String beanName = source + "BeetlSqlDataSourceBean";
		if (!registry.containsBeanDefinition(beanName)) {
			BeanDefinitionBuilder sqlSourceBuilder = registerBeetlSqlSource(source, slaveSource);
			registry.registerBeanDefinition(beanName, sqlSourceBuilder.getBeanDefinition());
		}
	}

	private BeanDefinitionBuilder registerBeetlSqlSource(String source, String slaveSource) {
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(ConnectionSourceFactory.class);

		bdb.addPropertyValue("masterSource", source);

		if(slaveSource == null){
			return  bdb;
		}

		String[] slaveSources = slaveSource.split(",");
		bdb.addPropertyValue("slaveSource", slaveSources);
		return bdb;
	}

	private void readySqlManager() {
		final ClassLoader classLoader = getClassLoader();
		beetlSqlPropertyMap.forEach((sqlManagerName, property) -> {
			if (property.getDynamicConnectionSource() != null ||
					property.getDynamicConnectionSourceProvider() != null) {
				// 支持 ConditionalConnectionSource  sqlManager
				registerDynamicConnectionSourceSQLManager(sqlManagerName, property, classLoader);
			} else if (property.getDynamicSqlManager() == null) {
				// 普通 sqlManager
				registerSQLManager(sqlManagerName, property, classLoader);
			}
		});

		beetlSqlPropertyMap.forEach((sqlManagerName, property) -> {
			if (property.getDynamicSqlManager() != null) {
				// 支持 ConditionalSQLManager  sqlManager
				registerDynamicSQLManager(sqlManagerName, property);
			}
		});
	}

	/**
	 * ConditionalSQLManager 方式，支持多 SQLManager
	 */
	private void registerDynamicSQLManager(String name, BeetlSqlProperty property){
		String[] sqlManagers = property.getDynamicSqlManager().split(",");

		List<String> managersList = new ArrayList<>(Arrays.asList(sqlManagers));

		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(DynamicSqlManagerFactoryBean.class);

		bdb.addPropertyValue("all", managersList);
		bdb.addPropertyValue("defaultSQLManager", managersList.get(0));
		bdb.addPropertyValue("conditional", property.getDynamicCondition());
		bdb.addPropertyValue("name", name);

		if (beetlSqlProperties.getPrimary() != null && beetlSqlProperties.getPrimary().equals(name)) {
			bdb.getBeanDefinition().setPrimary(true);
		}

		registry.registerBeanDefinition(name, bdb.getBeanDefinition());
	}

	/**
	 * ConditionalConnectionSource 方式，支持多数据库连接
	 */
	private void registerDynamicConnectionSourceSQLManager(String name, BeetlSqlProperty property, ClassLoader classLoader) {
		String[] connectionSources = new String[0];

		if (property.getDynamicConnectionSourceProvider() != null && property.getDynamicConnectionSourceGroup() != null ) {
			String[] groupCodes = property.getDynamicConnectionSourceGroup().split(",");
			String dynamicDatasourceProvider = property.getDynamicConnectionSourceProvider();
			Object instance = ObjectUtil.tryInstance(dynamicDatasourceProvider, classLoader);
			try {
				DynamicConnectionSourceGroup connectionSourceGroup = (DynamicConnectionSourceGroup) instance;
				connectionSourceGroup.setGroupCodes(groupCodes);
				DynamicConnectionSourceProvider provider = (DynamicConnectionSourceProvider) instance;
				connectionSources = provider.getConnectionSources();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (property.getDynamicConnectionSourceProvider() != null) {
			String dynamicDatasourceProvider = property.getDynamicConnectionSourceProvider();
			try {
				DynamicConnectionSourceProvider provider = (DynamicConnectionSourceProvider) ObjectUtil.tryInstance(dynamicDatasourceProvider, classLoader);
				connectionSources = provider.getConnectionSources();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			// if (property.getDynamicConnectionSource() != null)
			connectionSources = property.getDynamicConnectionSource().split(",");
		}

		registerSQLManager(name, property, classLoader, connectionSources[0]);
		// connectionSources 在 DynamicBeetlSqlAutoConfiguration 中用
		// setConditionalConnectionSource 设置
	}

	private  ClassLoader  getClassLoader(){
		 ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null) {
			classLoader = this.getClass().getClassLoader();
		}
		return classLoader;
	}

	/**
	 * 注册 sqlManager
	 */
	private BeanDefinitionBuilder registerSQLManager(String name, BeetlSqlProperty property, ClassLoader classLoader) {
		String csName = property.getDs();
		return registerSQLManager(name, property, classLoader, csName);
	}

	/**
	 * 注册 sqlManager
	 */
	private BeanDefinitionBuilder registerSQLManager(String name, BeetlSqlProperty property, ClassLoader classLoader, String csName) {
		String beetlSqlSourceBeanName = csName + "BeetlSqlDataSourceBean";

		MarkdownClasspathLoader loader = new MarkdownClasspathLoader(property.getSqlPath(), property.getSqlFileCharset());
		if (!registry.containsBeanDefinition(beetlSqlSourceBeanName)) {
			BeanDefinitionBuilder sqlSourceBuilder = registerBeetlSqlSource(csName, property.getSlave());
			registry.registerBeanDefinition(beetlSqlSourceBeanName, sqlSourceBuilder.getBeanDefinition());
		}
		Properties ps = new Properties();
		ps.put("PRODUCT_MODE", property.getDev() ? "false" : "true");
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(SqlManagerFactoryBean.class);
		bdb.addPropertyValue("cs", new RuntimeBeanReference(beetlSqlSourceBeanName));
		bdb.addPropertyValue("dbStyle", ObjectUtil.tryInstance(property.getDbStyle(), classLoader));
		bdb.addPropertyValue("interceptors", property.getDev() ? new Interceptor[] { new DebugInterceptor() } : new Interceptor[0]);
		bdb.addPropertyValue("sqlLoader", loader);
		bdb.addPropertyValue("nc", ObjectUtil.tryInstance(property.getNameConversion(), classLoader));
		bdb.addPropertyValue("extProperties", ps);
		bdb.addPropertyValue("name", name);

		if (beetlSqlProperties.getPrimary() != null && beetlSqlProperties.getPrimary().equals(name)) {
			bdb.getBeanDefinition().setPrimary(true);
		}

		registry.registerBeanDefinition(name, bdb.getBeanDefinition());

		BeetlSqlClassPathScanner scanner = new BeetlSqlClassPathScanner(registry);
		// this check is needed in Spring 3.1
		if (resourceLoader != null) {
			scanner.setResourceLoader(resourceLoader);
		}

		scanner.setSqlManagerFactoryBeanName(name);
		scanner.setSuffix(property.getDaoSuffix());
		scanner.registerFilters();
		scanner.scan(property.getBasePackage().split(","));
		return bdb;
	}

	/**
	 * 移除 bean
	 *
	 */
	private void removeBeanDefinitions(String name) {
		registry.removeBeanDefinition(name + "SqlManagerFactoryBean");
		registry.removeBeanDefinition(name + "BeetlSqlDataSourceBean");
	}
}
