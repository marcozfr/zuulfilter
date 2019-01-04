package com.netflix.zuul.sample.service;

import com.google.gson.annotations.SerializedName;

public class APIMTokenResponse {
	
	@SerializedName("access_token")
	private String accessToken;
	
	@SerializedName("token_type")
	private String tokenType;
	
	@SerializedName("expires_in")
	private String expiresIn;
	
	private String scope;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	

}
