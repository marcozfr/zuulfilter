package com.netflix.zuul.sample;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.netflix.zuul.BasicFilterUsageNotifier;
import com.netflix.zuul.DynamicCodeCompiler;
import com.netflix.zuul.FilterFactory;
import com.netflix.zuul.FilterUsageNotifier;
import com.netflix.zuul.filters.ZuulFilter;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.guice.GuiceFilterFactory;
import com.netflix.zuul.sample.filters.inbound.APIMAuthorizationFilter;
import com.netflix.zuul.sample.filters.inbound.ForwardFilter;
import com.netflix.zuul.sample.filters.inbound.Routes;
import com.netflix.zuul.sample.service.APIMAuthorizationService;


public class ZuulClasspathFiltersModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DynamicCodeCompiler.class).to(GroovyCompiler.class);
        bind(FilterFactory.class).to(GuiceFilterFactory.class);

        bind(FilterUsageNotifier.class).to(BasicFilterUsageNotifier.class);

        Multibinder<ZuulFilter> filterMultibinder = Multibinder.newSetBinder(binder(), ZuulFilter.class);
        filterMultibinder.addBinding().to(Routes.class);
        filterMultibinder.addBinding().toInstance(new ForwardFilter());
        
        Gson gson = new Gson();
        
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(500).build();
        
        CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClientBuilder.create()
        		.setDefaultRequestConfig(requestConfig).setDefaultRequestConfig(requestConfig)
                .setMaxConnPerRoute(20)
                .setMaxConnTotal(50)
                .build();
        
        httpAsyncClient.start();
        
        HttpClient httpClient = HttpClientBuilder.create().build(); 
        
        APIMAuthorizationService apimAuthorizationService = new APIMAuthorizationService(httpAsyncClient, httpClient, gson);
        APIMAuthorizationFilter apimAuthorizationFilter = new APIMAuthorizationFilter(apimAuthorizationService);
        filterMultibinder.addBinding().toInstance(apimAuthorizationFilter);
    }
}
