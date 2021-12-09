package com.qeedata.data.tenant.configuration;

import com.baomidou.dynamic.datasource.processor.DsHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.qeedata.data.tenant.processor.DsGroupProcessor;
import com.qeedata.data.tenant.processor.DsTenantProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 扩展 @DS 注解 DsTenantProcessor
 * @author adanz
 * @date 2020-03-31
 */
@Configuration
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class})
public class TenantDsConfig {
    @Bean
    @ConditionalOnMissingBean
    public DsProcessor dsProcessor() {
        DsGroupProcessor groupProcessor = new DsGroupProcessor();
        DsTenantProcessor tenantProcessor = new DsTenantProcessor();
        DsHeaderProcessor headerProcessor = new DsHeaderProcessor();
        DsSessionProcessor sessionProcessor = new DsSessionProcessor();
        DsSpelExpressionProcessor spelExpressionProcessor = new DsSpelExpressionProcessor();
        groupProcessor.setNextProcessor(tenantProcessor);
        tenantProcessor.setNextProcessor(headerProcessor);
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(spelExpressionProcessor);
        return groupProcessor;
    }
}
