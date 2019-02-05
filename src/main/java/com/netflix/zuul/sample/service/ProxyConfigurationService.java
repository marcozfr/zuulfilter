package com.netflix.zuul.sample.service;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.sample.dao.ProxyConfigurationDAO;
import com.netflix.zuul.sample.to.ProxyRoute;

public class ProxyConfigurationService {
	
	private static Logger logger = LoggerFactory.getLogger(ProxyConfigurationService.class);
	
	private ScheduledExecutorService executorService;
	
	private ConcurrentHashMap<String, ProxyRoute> proxyRoutesMap;
	
	private ProxyConfigurationDAO proxyConfigurationDAO;
	
	@Inject
	public ProxyConfigurationService(ScheduledExecutorService executorService, ProxyConfigurationDAO proxyConfigurationDAO) {
		this.executorService = executorService;
		this.proxyConfigurationDAO = proxyConfigurationDAO;
		this.proxyRoutesMap = new ConcurrentHashMap<>();
		reloadProxyRoutes();
	}
	
	public ConcurrentHashMap<String, ProxyRoute> getProxyRoutesMap() {
		return proxyRoutesMap;
	}

	private void reloadProxyRoutes() {
		loadProxyRoutes();
		executorService.scheduleAtFixedRate(() -> {
			loadProxyRoutes();
		}, 1000, 10*60*1000, TimeUnit.MILLISECONDS);
	}
	
	private void loadProxyRoutes() {
		logger.info("Reloading proxy routes");
		List<ProxyRoute> proxyRoutes;
		try {
			proxyRoutes = proxyConfigurationDAO.getProxyRoutes();
			for (ProxyRoute proxyRoute : proxyRoutes) {
				proxyRoutesMap.put(proxyRoute.getOrigin(), proxyRoute);
			}
		} catch (SQLException e) {
			logger.error("Could not load proxy routes from database. Reason: {} ", e.getMessage());
		}
		
	}

}
