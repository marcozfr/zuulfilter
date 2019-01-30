package com.netflix.zuul.sample.to;

import java.time.LocalDateTime;

import com.google.gson.annotations.SerializedName;

public class APIMTokenResponse {
	
	@SerializedName("access_token")
	private String accessToken;
	
	@SerializedName("token_type")
	private String tokenType;
	
	@SerializedName("expires_in")
	private Long expiresIn;
	
	private String scope;
	
	private LocalDateTime expirationDateTime;

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


	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public LocalDateTime getExpirationDateTime() {
		return expirationDateTime;
	}

	public void setExpirationDateTime(LocalDateTime expirationDateTime) {
		this.expirationDateTime = expirationDateTime;
	}

}
