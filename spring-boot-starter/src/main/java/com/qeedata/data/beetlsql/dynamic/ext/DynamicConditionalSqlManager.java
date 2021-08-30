package com.qeedata.data.beetlsql.dynamic.ext;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.beetl.sql.annotation.entity.TargetSQLManager;
import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SqlId;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * ConditionalDynamicSqlManager
 * 支援 pojo 为空情况，统一处理
 * @author adanz
 * @since 2020-12-03
 */
public class DynamicConditionalSqlManager extends ConditionalSQLManager {

    private final SQLManager defaultSQLManager;
    private final Map<String,SQLManager> sqlManagerMap;
    private final Conditional conditional = new DefaultConditional();

    public static class DefaultConditional  implements  Conditional{

        @Override
        public SQLManager decide(Class pojo,SQLManager defaultSQLManager, Map<String, SQLManager> sqlManagerMap) {
            String sqlManagerName;
            TargetSQLManager an = null;
            if (pojo != null) {
                an = (TargetSQLManager) pojo.getAnnotation(TargetSQLManager.class);
            }
            if (an != null) {
                // 优化使用 TargetSQLManager 注解，如果有时
                sqlManagerName = an.value();
            } else {
                // 否则按当前数据源
                sqlManagerName = DynamicDataSourceContextHolder.peek();
            }

            if (StringUtils.isEmpty(sqlManagerName))  {
                return defaultSQLManager;
            } else {
                SQLManager target = sqlManagerMap.get(sqlManagerName);
                if (target == null) {
                    throw new IllegalArgumentException("未发现目标sqlManager " + sqlManagerName + " from " + sqlManagerMap.keySet());
                } else {
                    return target;
                }
            }
        }
    }

    public DynamicConditionalSqlManager(SQLManager defaultSQLManager, Map<String,SQLManager> sqlManagerMap) {
        super(defaultSQLManager, sqlManagerMap);
        this.defaultSQLManager = defaultSQLManager;
        this.sqlManagerMap = sqlManagerMap;
    }

    @Override
    protected SQLManager decide(SqlId sqlId){
        return conditional.decide(null, defaultSQLManager, sqlManagerMap);
    }
}
