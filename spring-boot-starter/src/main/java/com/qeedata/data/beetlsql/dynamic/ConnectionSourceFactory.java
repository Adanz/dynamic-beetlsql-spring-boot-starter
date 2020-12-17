package com.qeedata.data.beetlsql.dynamic;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.beetl.sql.ext.spring.SpringConnectionSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;

/**
 * 支持 SpringConnectionSource, 参考官方代码
 * @author adanz
 * @since 2020-12-03
 */
public class ConnectionSourceFactory implements FactoryBean<SpringConnectionSource>, ApplicationContextAware {
    private static String dataSourceBeanName = "dataSource";
    private ApplicationContext applicationContext = null;
    private SpringConnectionSource connectionSource = null;
    private String masterSource;
    private String[] slaveSource;

    @Override
    public SpringConnectionSource getObject() throws Exception {
        if(connectionSource != null) {
            return connectionSource;
        }

        DynamicRoutingDataSource ds = (DynamicRoutingDataSource)applicationContext.getBean(dataSourceBeanName);
        SpringConnectionSource cs = new SpringConnectionSource();
        cs.setMasterSource(ds.getDataSource(masterSource));

        if (slaveSource != null){
            DataSource[] slaves = new DataSource[slaveSource.length];
            int i =0;
            for(String name : slaveSource){
                slaves[i++] = ds.getDataSource(name);
            }
            cs.setSlaveSource(slaves);
        }

        connectionSource = cs;

        return cs;
    }

    @Override
    public Class<?> getObjectType() {
        return SpringConnectionSource.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getMasterSource() {
        return masterSource;
    }

    public void setMasterSource(String masterSource) {
        this.masterSource = masterSource;
    }

    public String[] getSlaveSource() {
        return slaveSource;
    }

    public void setSlaveSource(String[] slaveSource) {
        this.slaveSource = slaveSource;
    }
}
