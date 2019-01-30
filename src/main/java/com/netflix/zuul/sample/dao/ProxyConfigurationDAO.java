package com.netflix.zuul.sample.dao;

import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.zuul.sample.to.ProxyRoute;

@Singleton
public class ProxyConfigurationDAO {
	
	private DataSource dataSource;
	
	@Inject
	public ProxyConfigurationDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<ProxyRoute> getProxyRoutes() {
		return Collections.emptyList();
	}

}
