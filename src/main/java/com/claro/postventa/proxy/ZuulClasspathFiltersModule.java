package com.claro.postventa.proxy;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.security.auth.login.Configuration;
import javax.sql.DataSource;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;

import com.claro.postventa.proxy.dao.ProxyConfigurationDAO;
import com.claro.postventa.proxy.filters.APIMAuthorizationFilter;
import com.claro.postventa.proxy.filters.ForwardFilter;
import com.claro.postventa.proxy.filters.InboundLoggingFilter;
import com.claro.postventa.proxy.filters.OutboundLoggingFilter;
import com.claro.postventa.proxy.filters.Routes;
import com.claro.postventa.proxy.service.APIMAuthorizationService;
import com.claro.postventa.proxy.service.ProxyConfigurationService;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.netflix.config.ConfigurationManager;
import com.netflix.zuul.BasicFilterUsageNotifier;
import com.netflix.zuul.DynamicCodeCompiler;
import com.netflix.zuul.FilterFactory;
import com.netflix.zuul.FilterUsageNotifier;
import com.netflix.zuul.filters.ZuulFilter;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.guice.GuiceFilterFactory;
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
        hikariConfig.setJdbcUrl(ConfigurationManager.getConfigInstance().getString("com.claro.config.proxyroutes.jdbc.url"));
        hikariConfig.setUsername(ConfigurationManager.getConfigInstance().getString("com.claro.config.proxyroutes.jdbc.username"));
        hikariConfig.setPassword(ConfigurationManager.getConfigInstance().getString("com.claro.config.proxyroutes.jdbc.password"));
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
        filterMultibinder.addBinding().to(InboundLoggingFilter.class);
        filterMultibinder.addBinding().to(OutboundLoggingFilter.class);
        
    }
}
