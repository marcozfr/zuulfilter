package com.claro.postventa.proxy.to;

public class ProxyRoute {
	
	private Integer routeId;
	private String destination;
	private String origin;
	private Integer timeout;
	
	public ProxyRoute(Integer routeId, String origin, String destination, Integer timeout) {
		super();
		this.routeId = routeId;
		this.destination = destination;
		this.origin = origin;
		this.timeout = timeout;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public long getTimeout() {
		return timeout;
	}
	public Integer getRouteId() {
		return routeId;
	}
	public void setRouteId(Integer routeId) {
		this.routeId = routeId;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
}
