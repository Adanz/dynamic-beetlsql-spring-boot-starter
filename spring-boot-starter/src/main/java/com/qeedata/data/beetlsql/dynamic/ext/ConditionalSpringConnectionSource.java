package com.qeedata.data.beetlsql.dynamic.ext;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.ext.spring.SpringConnectionSource;

import java.sql.Connection;
import java.util.Map;

/**
 * 多个数据源的代理，支持 SpringConnectionSource, 参考官方代码
 * @author adanz
 * @since 2020-12-03
 */
public class ConditionalSpringConnectionSource extends SpringConnectionSource {

    Policy policy;
    Map<String, SpringConnectionSource> all;
    ConnectionSource defaultCs;

    /**
     *
     * @param policy 选择数据源的策略
     * @param all 所有备选的数据源表
     */
    public ConditionalSpringConnectionSource(Policy policy, Map<String,SpringConnectionSource> all){
        this.all = all;
        String defaultName = policy.getMasterName();
        if (defaultName == null) {
            defaultName = this.all.keySet().toArray()[0].toString();
        }
        defaultCs = all.get(defaultName);
        if(defaultCs==null){
            throw new IllegalArgumentException("根据 "+defaultName+" 找不到对应的ConnectionSource");
        }
        this.policy = policy;
    }
    @Override
    public Connection getMasterConn() {
        // return defaultCs.getMasterConn();
        // 根据策略取连接 2022-03-17
        return this.getConn(null, true);
    }

    @Override
    public Connection getMetaData() {
        return defaultCs.getMasterConn();
    }

    @Override
    public Connection getConn(ExecuteContext ctx, boolean isUpdate) {
        String name = policy.getConnectionSourceName(ctx,isUpdate);
        ConnectionSource cs = all.get(name);
        if(cs==null){
            throw new IllegalArgumentException("根据 "+name+" 找不到对应的ConnectionSource");
        }
        return cs.getConn(ctx,isUpdate);

    }

    public interface Policy{
        String getConnectionSourceName(ExecuteContext ctx, boolean isUpdate);
        default String getMasterName() {return null;};
    }
}
