package com.qeedata.data.beetlsql.dynamic;

import org.beetl.sql.core.SQLManager;

/**
 * 对sqlManager定制
 * @author xiandafu
 */
public interface SqlManagerCustomize {
	void customize(String sqlManagerName , SQLManager manager);
}
