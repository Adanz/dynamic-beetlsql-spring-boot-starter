package com.qeedata.sample.dynamic.beetlsql.config;

import com.qeedata.data.beetlsql.dynamic.provider.DynamicConnectionSourceProvider;

public class DemoConnectionSourceProvider implements DynamicConnectionSourceProvider {
    @Override
    public String[] getConnectionSources() {
        String[] sources = new String[]{"ds3", "ds4"};
        return sources;
    }
}
