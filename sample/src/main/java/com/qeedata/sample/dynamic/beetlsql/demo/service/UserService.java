package com.qeedata.sample.dynamic.beetlsql.demo.service;

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

    @Autowired
    private DataSource ds;

    /**
     * 默认由 dynamic.beetlsql.primary = cs
     */
    @Autowired
    private SQLManager csManager;

    @Autowired
    @Qualifier("ds1")
    private SQLManager sqlManager1;

    @Autowired
    @Qualifier("ds2")
    private SQLManager sqlManager2;

    @Autowired
    @Qualifier("sm")
    private SQLManager smManager;

    @Autowired
    @Qualifier("ccs")
    private SQLManager ccsManager;

    /**
     * 使用 Dynamic Datasource 注解切换
     */
    @DS("ds2")
    public List getUserList() {
        SqlId sqlId = SqlId.of("demo", "getUser");
        // 代码切换
        /// DynamicDataSourceContextHolder.push("ds2");
        /// List<?> rows1 = sqlManager1.select(sqlId, Map.class, null);
        /// List<?> rows2 = sqlManager2.select(SqlId.of("demo.getUser"), Map.class, null);

        List<?> rows = csManager.select(sqlId, Map.class, null);

        return rows;
    }

    @DS("ds3")
    public List getUserList2() {
        SqlId sqlId = SqlId.of("demo", "getUser");
        List<?> rows = ccsManager.select(sqlId, Map.class, null);
        return rows;
    }

    /**
     * 使用默认事务管理器，见 DbConfig
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser() {
        SqlId sqlId = SqlId.of("demo", "updateUser");

        csManager.update(sqlId, null);

        int i = 10 / 0;
    }

    /**
     * 使用 ds2, 需指定事务管理器 ds2TransactionManager，见 DbConfig
     */
    @Transactional(value = "ds2TransactionManager", rollbackFor = Exception.class)
    public void updateUser1() {
        SqlId sqlId = SqlId.of("demo", "updateUser");

        sqlManager2.update(sqlId, null);

        int i = 10 / 0;
    }

    public List getBeans() {
        String[] beans = applicationContext.getBeanDefinitionNames();
        Arrays.sort(beans);
        List<String> results = new ArrayList(Arrays.asList(beans));

        return results;
    }

}
