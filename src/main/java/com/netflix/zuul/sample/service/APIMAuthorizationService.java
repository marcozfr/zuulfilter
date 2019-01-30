package com.netflix.zuul.sample.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.netflix.config.ConfigurationManager;
import com.netflix.zuul.sample.to.APIMTokenRequest;
import com.netflix.zuul.sample.to.APIMTokenResponse;

import rx.Observable;


public class APIMAuthorizationService {
	
	public static final Logger logger = LoggerFactory.getLogger(APIMAuthorizationService.class);
	
	private HttpClient httpClient;
	
	private Gson gson;
	
	private APIMTokenRequest apimTokenRequest;
	
	private APIMTokenResponse apimTokenResponse;
	
	private ScheduledExecutorService executorService;
	
	@Inject
	public APIMAuthorizationService(HttpClient httpClient, ScheduledExecutorService executorService) {
		this.gson = new Gson();
		this.apimTokenRequest = new APIMTokenRequest();
		this.apimTokenRequest.setClientId("2d010a10-7ed9-4fbc-abc1-455eae702a58");
		this.apimTokenRequest.setClientSecret("f50b36c7-0c3f-47d0-bcb3-ee79a0f47082");
		this.httpClient = httpClient;
		this.apimTokenResponse = new APIMTokenResponse();
		this.executorService = executorService;
		this.reloadToken();
	}
	
	public Observable<APIMTokenResponse> getAPIMTokenResponse() {
		synchronized (apimTokenResponse) {
			return Observable.just(apimTokenResponse);
		}
	}
	
	public void reloadToken(){
		synchronized (apimTokenResponse) {
			this.apimTokenResponse = requestToken();
		}
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				logger.info("Reloading token");
				reloadToken();
			}
		}, this.apimTokenResponse.getExpiresIn() / 2
			, TimeUnit.SECONDS);
	}
	
	public APIMTokenResponse requestToken() {
		String apimTokenRequestString = gson.toJson(apimTokenRequest);
		String apimAuthorizationUrl = ConfigurationManager.getConfigInstance()
				.getString("com.claro.config.authorization.apim.url");
		HttpPost httpPost = new HttpPost(apimAuthorizationUrl);
		httpPost.setEntity(new StringEntity(apimTokenRequestString,"utf-8"));
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			APIMTokenResponse apimTokenResponse = gson.fromJson(new InputStreamReader(
					httpEntity.getContent()), APIMTokenResponse.class);
			apimTokenResponse.setExpirationDateTime(LocalDateTime.now().plusSeconds(apimTokenResponse.getExpiresIn()));
			logger.info("Got token :" + apimTokenResponse.getAccessToken());
			return apimTokenResponse;
		} catch (ClientProtocolException e) {
			logger.error("Client protocol exception", e);
		} catch (IOException e) {
			logger.error("io exception", e);
		} finally {
			httpPost.releaseConnection();
		}
		return null;
	}

}
