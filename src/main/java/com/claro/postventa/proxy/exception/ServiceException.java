package com.claro.postventa.proxy.exception;

public class ServiceException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7541738747409168871L;
	
	private String statusCode;
	
	public ServiceException() {
		super();
	}
	public ServiceException(String message, String statusCode, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}
	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, String statusCode) {
		super(message);
		this.statusCode = statusCode;
	}
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
