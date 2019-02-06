package com.netflix.zuul.sample.filters;

import com.claro.postventa.proxy.FiltersRegisteringService;
import com.claro.postventa.proxy.ZuulClasspathFiltersModule;
import com.google.inject.Injector;
import com.netflix.governator.InjectorBuilder;
import com.netflix.zuul.filters.ZuulFilter;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FiltersRegisteringServiceTest {

    @Test
    public void testFiltersLoading() {
        Injector injector = InjectorBuilder.fromModule(new ZuulClasspathFiltersModule()).createInjector();
        
        FiltersRegisteringService service = injector.getInstance(FiltersRegisteringService.class);
        List<ZuulFilter> filters = service.getFilters();
        assertThat(filters).isNotNull();
    }
}
