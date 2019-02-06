package com.claro.postventa.proxy.filters;

import com.claro.postventa.proxy.service.ProxyConfigurationService;
import com.claro.postventa.proxy.to.ProxyRoute;
import com.google.inject.Inject;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.http.HttpInboundSyncFilter;
import com.netflix.zuul.message.http.HttpRequestMessage;

public class ForwardFilter extends HttpInboundSyncFilter {
	
	private ProxyConfigurationService proxyConfigurationService;
	
	@Inject
	public ForwardFilter(ProxyConfigurationService proxyConfigurationService) {
		this.proxyConfigurationService = proxyConfigurationService;
	}

    @Override
    public HttpRequestMessage apply(HttpRequestMessage input) {
        SessionContext context = input.getContext();
		ProxyRoute proxyRoute = proxyConfigurationService.getProxyRoutesMap().get(input.getPath());
        if(proxyRoute!=null && proxyRoute.getDestination()!=null) {
        	context.set("overrideURI", proxyRoute.getDestination());
        }
        return input;
    }

    @Override
    public int filterOrder() {
        return 503;
    }

    @Override
    public boolean shouldFilter(HttpRequestMessage input) {
    	ProxyRoute proxyRoute = proxyConfigurationService.getProxyRoutesMap().get(input.getPath());
        if(proxyRoute!=null && proxyRoute.getDestination()!=null) {
        	return true;
        }
        return false;
    }
}
