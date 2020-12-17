package com.qeedata.data.beetlsql.dynamic;

import lombok.Data;
import org.beetl.sql.clazz.kit.StringKit;
import org.springframework.core.env.Environment;

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
    public static String PREFIX_DEFAULT = PREFIX_BEETLSQL + "._default";
    public static String PREFIX_SQL_MANAGERS = PREFIX_BEETLSQL + ".sqlManagers";
    public static String PREFIX_PRIMARY = PREFIX_BEETLSQL + ".primary";

    public static String PREFIX_DATASOURCE = ".ds";
    public static String PREFIX_BASE_PACKAGE = ".basePackage";
    public static String PREFIX_DAO_SUFFIX = ".daoSuffix";
    public static String PREFIX_SQL_PATH = ".sqlPath";
    public static String PREFIX_NAME_CONVERSION = ".nameConversion";
    public static String PREFIX_DB_STYLE = ".dbStyle";
    public static String PREFIX_DEV = ".dev";
    public static String PREFIX_SLAVE = ".slave";
    public static String PREFIX_DYNAMIC_CONDITION = ".dynamicCondition ";
    public static String PREFIX_DYNAMIC_SQLMANAGER = ".dynamicSqlManager";
    public static String PREFIX_DYNAMIC_CONNECTION_SOURCE = ".dynamicConnectionSource";

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
        property.setNameConversion(env.getProperty(prefix + PREFIX_NAME_CONVERSION, "org.beetl.sql.core.UnderlinedNameConversion"));
        property.setDbStyle(env.getProperty(prefix + PREFIX_DB_STYLE, "org.beetl.sql.core.db.MySqlStyle"));
        property.setDev(Boolean.parseBoolean(env.getProperty(prefix + PREFIX_DEV, "true")));
        property.setDs(env.getProperty(prefix + PREFIX_DATASOURCE));

        property.setSlave(env.getProperty(prefix + PREFIX_SLAVE));
        property.setDynamicCondition(env.getProperty(prefix + PREFIX_DYNAMIC_CONDITION));
        property.setDynamicSqlManager(env.getProperty(prefix + PREFIX_DYNAMIC_SQLMANAGER));
        property.setDynamicConnectionSource(env.getProperty(prefix + PREFIX_DYNAMIC_CONNECTION_SOURCE));

        return property;
    }

    private BeetlSqlProperty MergeBeetlSqlProperty(String name, BeetlSqlProperty property, BeetlSqlProperty defaultProperty) {
        BeetlSqlProperty prop = new BeetlSqlProperty();

        String ds = getProperty(property.getDs(), name);
        String basePackage = getProperty(property.getBasePackage(), defaultProperty.getBasePackage());
        String daoSuffix = getProperty(property.getDaoSuffix(), defaultProperty.getDaoSuffix());
        String sqlPath = getProperty(property.getSqlPath(), defaultProperty.getSqlPath());
        String nameConversion = getProperty(property.getNameConversion(), defaultProperty.getNameConversion());
        String dbStyle = getProperty(property.getDbStyle(), defaultProperty.getDbStyle());
        String slave = getProperty(property.getSlave(), defaultProperty.getSlave());
        String dynamicConnectionSource = getProperty(property.getDynamicConnectionSource(), defaultProperty.getDynamicConnectionSource());
        String dynamicSqlManager = getProperty(property.getDynamicSqlManager(), defaultProperty.getDynamicSqlManager());
        String dynamicCondition = getProperty(property.getDynamicCondition(), defaultProperty.getDynamicCondition());
        Boolean dev = getProperty(property.getDev(), defaultProperty.getDev());

        if(!StringKit.isEmpty(dynamicSqlManager)){
            if(StringKit.isEmpty(dynamicCondition)){
                dynamicCondition = DynamicConditionalSqlManager.DefaultConditional.class.getName();
            }
        }

        prop.setDs(ds);
        prop.setBasePackage(basePackage);
        prop.setDaoSuffix(daoSuffix);
        prop.setSqlPath(sqlPath);
        prop.setNameConversion(nameConversion);
        prop.setDbStyle(dbStyle);
        prop.setSlave(slave);
        prop.setDynamicSqlManager(dynamicSqlManager);
        prop.setDynamicConnectionSource(dynamicConnectionSource);
        prop.setDynamicCondition(dynamicCondition);
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
