package com.netflix.zuul.sample;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.sql.DataSource;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.netflix.zuul.BasicFilterUsageNotifier;
import com.netflix.zuul.DynamicCodeCompiler;
import com.netflix.zuul.FilterFactory;
import com.netflix.zuul.FilterUsageNotifier;
import com.netflix.zuul.filters.ZuulFilter;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.guice.GuiceFilterFactory;
import com.netflix.zuul.sample.dao.ProxyConfigurationDAO;
import com.netflix.zuul.sample.filters.inbound.APIMAuthorizationFilter;
import com.netflix.zuul.sample.filters.inbound.ForwardFilter;
import com.netflix.zuul.sample.filters.inbound.Routes;
import com.netflix.zuul.sample.service.APIMAuthorizationService;
import com.netflix.zuul.sample.service.ProxyConfigurationService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class ZuulClasspathFiltersModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DynamicCodeCompiler.class).to(GroovyCompiler.class);
        bind(FilterFactory.class).to(GuiceFilterFactory.class);

        bind(FilterUsageNotifier.class).to(BasicFilterUsageNotifier.class);

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        
        bind(ScheduledExecutorService.class).toInstance(scheduledThreadPoolExecutor);
        
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://dbauroraclarodev.cx053an4mesk.us-east-1.rds.amazonaws.com:3306/dbclarohogardev");
        hikariConfig.setUsername("userclarohogar");
        hikariConfig.setPassword("us3rcl4r0h0g4r2018*");
        hikariConfig.addDataSourceProperty("cachePrepStmts","true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize","250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit","2048");
        DataSource configDatasource = new HikariDataSource(hikariConfig);
        
        bind(DataSource.class).toInstance(configDatasource);
        
        HttpClient httpClient = HttpClientBuilder.create()
        		.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        		.build();
        
        bind(HttpClient.class).toInstance(httpClient);
        
        bind(ProxyConfigurationDAO.class);
        
        bind(APIMAuthorizationService.class);
        
        bind(ProxyConfigurationService.class);
        
        Multibinder<ZuulFilter> filterMultibinder = Multibinder.newSetBinder(binder(), ZuulFilter.class);
        filterMultibinder.addBinding().to(Routes.class);
        filterMultibinder.addBinding().to(ForwardFilter.class);
        filterMultibinder.addBinding().to(APIMAuthorizationFilter.class);
        
    }
}
