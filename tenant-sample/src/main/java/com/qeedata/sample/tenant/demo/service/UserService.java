package com.qeedata.sample.tenant.demo.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SqlId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 测试
 * @author adanz
 */
@Service
@Lazy
public class UserService  {
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 默认由 dynamic.beetlsql.primary = orderDb
     */
    @Autowired
    private SQLManager orderSqlManager;

    @Autowired
    @Qualifier("stockDb")
    private SQLManager stockSqlManager;

    /**
     * 使用 Dynamic Datasource 注解切换
     */
    public List getUserList() {
        SqlId sqlId = SqlId.of("demo", "getUser");

        List<?> rows = orderSqlManager.select(sqlId, Map.class, null);

        return rows;
    }

    public List getUserList2() {
        SqlId sqlId = SqlId.of("demo", "getUser");
        List<?> rows = stockSqlManager.select(sqlId, Map.class, null);
        return rows;
    }

    /**
     * 使用默认事务管理器，见 DbConfig
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser() {
        SqlId sqlId = SqlId.of("demo", "updateUser");

        orderSqlManager.update(sqlId, null);

        int i = 10 / 0;
    }


    public List getBeans() {
        String[] beans = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beans);
        List<String> results = new ArrayList(Arrays.asList(beans));

        return results;
    }

}
