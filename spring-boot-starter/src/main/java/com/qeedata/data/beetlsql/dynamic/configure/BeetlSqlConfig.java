package com.qeedata.data.beetlsql.dynamic.configure;

import com.qeedata.data.beetlsql.dynamic.ext.DynamicConditionalSqlManager;
import lombok.Data;
import org.beetl.sql.clazz.kit.StringKit;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author adanz
 * @since 2020-12-03
 */
@Data
public class BeetlSqlConfig {
    public static final String PREFIX_BEETLSQL = "dynamic.beetlsql";
    public static final String PREFIX_DEFAULT = PREFIX_BEETLSQL + "._default";
    public static final String PREFIX_SQL_MANAGERS = PREFIX_BEETLSQL + ".sqlManagers";
    public static final String PREFIX_PRIMARY = PREFIX_BEETLSQL + ".primary";

    public static final String PREFIX_DATASOURCE = ".ds";
    public static final String PREFIX_BASE_PACKAGE = ".basePackage";
    public static final String PREFIX_DAO_SUFFIX = ".daoSuffix";
    public static final String PREFIX_SQL_PATH = ".sqlPath";
    public static final String PREFIX_SQL_FILE_CHARSET = ".sqlFileCharset";
    public static final String PREFIX_NAME_CONVERSION = ".nameConversion";
    public static final String PREFIX_DB_STYLE = ".dbStyle";
    public static final String PREFIX_DEV = ".dev";
    public static final String PREFIX_SLAVE = ".slave";
    public static final String PREFIX_DYNAMIC_CONDITION = ".dynamicCondition ";
    public static final String PREFIX_DYNAMIC_SQLMANAGER = ".dynamicSqlManager";
    public static final String PREFIX_DYNAMIC_CONNECTION_SOURCE = ".dynamicConnectionSource";
    public static final String PREFIX_DYNAMIC_CONNECTION_POLICY = ".dynamicConnectionPolicy";
    public static final String PREFIX_DYNAMIC_CONNECTION_SOURCE_PROVIDER = ".dynamicConnectionSourceProvider";
    public static final String PREFIX_DYNAMIC_CONNECTION_SOURCE_GROUP = ".dynamicConnectionSourceGroup";

    public static final String DEFAULT_GROUP_CONNECTION_SOURCE_PROVIDER = "com.qeedata.data.tenant.group.DefaultConnectionSourceGroup";
    public static final String DEFAULT_GROUP_CONNECTION_POLICY = "com.qeedata.data.tenant.group.DefaultConnectionSourceGroup";

    private Environment env;
    private DynamicBeetlSqlProperties properties;

    public BeetlSqlConfig(Environment env){
        this.env = env;
        properties = new DynamicBeetlSqlProperties();
    }

    public List<String> getSqlManagers() {
        return Arrays.asList(env.getProperty(PREFIX_SQL_MANAGERS).split(","));
    }

    public String getPrimary() {
        return env.getProperty(PREFIX_PRIMARY);
    }

    public DynamicBeetlSqlProperties getProperties() {
        Map<String, BeetlSqlProperty> propertyMap = new HashMap<>(16);
        BeetlSqlProperty defaultProperty = getBeetlSqlProperty(PREFIX_DEFAULT);

        List<String> sqlManagers = getSqlManagers();
        for(String name : sqlManagers) {
            BeetlSqlProperty property = getBeetlSqlProperty(name);
            propertyMap.put(name, MergeBeetlSqlProperty(name, property, defaultProperty));
        }

        properties.setSqlManagers(sqlManagers);
        properties.setPrimary(getPrimary());
        properties.setBeetlsql(propertyMap);
        properties.setDefaultBeetlsql(defaultProperty);

        return properties;
    }

    private BeetlSqlProperty getBeetlSqlProperty(String name) {
        String prefix = PREFIX_BEETLSQL + "." + name;

        BeetlSqlProperty property = new BeetlSqlProperty();
        property.setBasePackage(env.getProperty(prefix + PREFIX_BASE_PACKAGE, "com"));
        property.setDaoSuffix(env.getProperty(prefix + PREFIX_DAO_SUFFIX, "Mapper"));
        property.setSqlPath(env.getProperty(prefix + PREFIX_SQL_PATH, "sql"));
        property.setSqlFileCharset(env.getProperty(prefix + PREFIX_SQL_FILE_CHARSET, Charset.defaultCharset().name()));
        property.setNameConversion(env.getProperty(prefix + PREFIX_NAME_CONVERSION, "org.beetl.sql.core.UnderlinedNameConversion"));
        property.setDbStyle(env.getProperty(prefix + PREFIX_DB_STYLE, "org.beetl.sql.core.db.MySqlStyle"));
        property.setDev(Boolean.parseBoolean(env.getProperty(prefix + PREFIX_DEV, "true")));
        property.setDs(env.getProperty(prefix + PREFIX_DATASOURCE));

        property.setSlave(env.getProperty(prefix + PREFIX_SLAVE));
        property.setDynamicCondition(env.getProperty(prefix + PREFIX_DYNAMIC_CONDITION));
        property.setDynamicSqlManager(env.getProperty(prefix + PREFIX_DYNAMIC_SQLMANAGER));
        property.setDynamicConnectionSource(env.getProperty(prefix + PREFIX_DYNAMIC_CONNECTION_SOURCE));

        String dynamicConnectionPolicy = env.getProperty(prefix + PREFIX_DYNAMIC_CONNECTION_POLICY);
        String dynamicConnectionSourceProvider = env.getProperty(prefix + PREFIX_DYNAMIC_CONNECTION_SOURCE_PROVIDER);
        String dynamicConnectionSourceGroup = env.getProperty(prefix + PREFIX_DYNAMIC_CONNECTION_SOURCE_GROUP);

        if (!StringUtils.isEmpty(dynamicConnectionSourceGroup)) {
            if (StringUtils.isEmpty(dynamicConnectionPolicy)) {
                dynamicConnectionPolicy = DEFAULT_GROUP_CONNECTION_POLICY;
            }
            if (StringUtils.isEmpty(dynamicConnectionSourceProvider)) {
                dynamicConnectionSourceProvider = DEFAULT_GROUP_CONNECTION_SOURCE_PROVIDER;
            }
        }

        property.setDynamicConnectionPolicy(dynamicConnectionPolicy);
        property.setDynamicConnectionSourceProvider(dynamicConnectionSourceProvider);
        property.setDynamicConnectionSourceGroup(dynamicConnectionSourceGroup);

        return property;
    }

    private BeetlSqlProperty MergeBeetlSqlProperty(String name, BeetlSqlProperty property, BeetlSqlProperty defaultProperty) {
        BeetlSqlProperty prop = new BeetlSqlProperty();

        String ds = getProperty(property.getDs(), name);
        String basePackage = getProperty(property.getBasePackage(), defaultProperty.getBasePackage());
        String daoSuffix = getProperty(property.getDaoSuffix(), defaultProperty.getDaoSuffix());
        String sqlPath = getProperty(property.getSqlPath(), defaultProperty.getSqlPath());
        String sqlFileCharset = getProperty(property.getSqlFileCharset(), defaultProperty.getSqlFileCharset());
        String nameConversion = getProperty(property.getNameConversion(), defaultProperty.getNameConversion());
        String dbStyle = getProperty(property.getDbStyle(), defaultProperty.getDbStyle());
        String slave = getProperty(property.getSlave(), defaultProperty.getSlave());
        String dynamicConnectionSource = getProperty(property.getDynamicConnectionSource(), defaultProperty.getDynamicConnectionSource());
        String dynamicSqlManager = getProperty(property.getDynamicSqlManager(), defaultProperty.getDynamicSqlManager());
        String dynamicCondition = getProperty(property.getDynamicCondition(), defaultProperty.getDynamicCondition());
        String dynamicConnectionSourceProvider = getProperty(property.getDynamicConnectionSourceProvider(), defaultProperty.getDynamicConnectionSourceProvider());
        String dynamicConnectionPolicy = getProperty(property.getDynamicConnectionPolicy(), defaultProperty.getDynamicConnectionPolicy());
        Boolean dev = getProperty(property.getDev(), defaultProperty.getDev());
        String dynamicConnectionSourceGroup = getProperty(property.getDynamicConnectionSourceGroup(), defaultProperty.getDynamicConnectionSourceGroup());

        if(!StringKit.isEmpty(dynamicSqlManager)){
            if(StringKit.isEmpty(dynamicCondition)){
                dynamicCondition = DynamicConditionalSqlManager.DefaultConditional.class.getName();
            }
        }

        prop.setDs(ds);
        prop.setBasePackage(basePackage);
        prop.setDaoSuffix(daoSuffix);
        prop.setSqlPath(sqlPath);
        prop.setSqlFileCharset(sqlFileCharset);
        prop.setNameConversion(nameConversion);
        prop.setDbStyle(dbStyle);
        prop.setSlave(slave);
        prop.setDynamicSqlManager(dynamicSqlManager);
        prop.setDynamicConnectionSource(dynamicConnectionSource);
        prop.setDynamicCondition(dynamicCondition);
        prop.setDynamicConnectionPolicy(dynamicConnectionPolicy);
        prop.setDynamicConnectionSourceProvider(dynamicConnectionSourceProvider);
        prop.setDynamicConnectionSourceGroup(dynamicConnectionSourceGroup);
        prop.setDev(dev);

        return prop;
    }

    private String getProperty(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    private Boolean getProperty(Boolean value, Boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

}
