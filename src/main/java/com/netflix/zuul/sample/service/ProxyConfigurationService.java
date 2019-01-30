package com.netflix.zuul.sample.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.netflix.zuul.sample.dao.ProxyConfigurationDAO;
import com.netflix.zuul.sample.to.ProxyRoute;

public class ProxyConfigurationService {
	
	private ScheduledExecutorService executorService;
	
	private ConcurrentHashMap<String, ProxyRoute> proxyRoutesMap;
	
	private ProxyConfigurationDAO proxyConfigurationDAO;
	
	@Inject
	public ProxyConfigurationService(ScheduledExecutorService executorService, ProxyConfigurationDAO proxyConfigurationDAO) {
		this.executorService = executorService;
		this.proxyConfigurationDAO = proxyConfigurationDAO;
		reloadProxyRoutes();
	}

	private void reloadProxyRoutes() {
		loadProxyRoutes();
		executorService.scheduleAtFixedRate(() -> {
			loadProxyRoutes();
		}, 1000, 10000, TimeUnit.MILLISECONDS);
	}
	
	private void loadProxyRoutes() {
		List<ProxyRoute> proxyRoutes = proxyConfigurationDAO.getProxyRoutes();
		for (ProxyRoute proxyRoute : proxyRoutes) {
			proxyRoutesMap.put(proxyRoute.getOrigin(), proxyRoute);
		}
	}

}
