package com.netflix.zuul.sample.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.netflix.config.ConfigurationManager;

import rx.Observable;
import rx.apache.http.ObservableHttp;


public class APIMAuthorizationService {
	
	public static final Logger logger = LoggerFactory.getLogger(APIMAuthorizationService.class);
	
	private HttpAsyncClient httpAsyncClient;
	
	private Gson gson;
	
	private APIMTokenRequest apimTokenRequest;
	
	public APIMAuthorizationService(HttpAsyncClient httpAsyncClient, Gson gson) {
		this.httpAsyncClient = httpAsyncClient;
		this.gson = gson;
		this.apimTokenRequest = new APIMTokenRequest();
		this.apimTokenRequest.setClientId("2d010a10-7ed9-4fbc-abc1-455eae702a58");
		this.apimTokenRequest.setClientSecret("f50b36c7-0c3f-47d0-bcb3-ee79a0f47082");
	}
	
	public Observable<APIMTokenResponse> requestTokenAsync() {
		String apimTokenRequestString = gson.toJson(apimTokenRequest);
		String apimAuthorizationUrl = ConfigurationManager.getConfigInstance()
				.getString("com.claro.config.authorization.apim.url");
		try {
			return ObservableHttp.createRequest(
					HttpAsyncMethods.createPost(apimAuthorizationUrl, 
							apimTokenRequestString, ContentType.APPLICATION_JSON),
					httpAsyncClient)
			.toObservable().flatMap(response->{
				return response.getContent().map((responseBytes -> {
					logger.info(new String(responseBytes));
					return gson.fromJson(new InputStreamReader(new ByteArrayInputStream(responseBytes)), APIMTokenResponse.class);
				}));
			}).doOnError(error -> {
				logger.error("Error while consuming generatoken " , error);
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}
