package com.claro.postventa.proxy.filters;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.claro.postventa.proxy.service.APIMAuthorizationService;
import com.netflix.config.ConfigurationManager;
import com.netflix.zuul.filters.http.HttpInboundFilter;
import com.netflix.zuul.message.HeaderName;
import com.netflix.zuul.message.Headers;
import com.netflix.zuul.message.http.HttpRequestMessage;

import rx.Observable;

public class APIMAuthorizationFilter extends HttpInboundFilter {
	
	public static final Logger logger = LoggerFactory.getLogger(APIMAuthorizationFilter.class);
	
	private APIMAuthorizationService apimAuthorizationService;
	
	@Inject
	public APIMAuthorizationFilter(APIMAuthorizationService apimAuthorizationService) {
		this.apimAuthorizationService = apimAuthorizationService;
	}

	@Override
	public int filterOrder() {
		return 502;
	}

	@Override
	public Observable<HttpRequestMessage> applyAsync(HttpRequestMessage input) {
		
		return apimAuthorizationService.getAPIMTokenResponse().map(apimTokenResponse -> {
			if(apimTokenResponse!=null && apimTokenResponse.getAccessToken()!=null) {
				logger.debug("Enrichment with token: {}", apimTokenResponse.getAccessToken());
				HeaderName accessTokenName = new HeaderName("access_token");
				if(input.getHeaders()!=null) {
					input.getHeaders().add(accessTokenName, apimTokenResponse.getAccessToken());
				} else {
					Headers newRequestHeaders = new Headers();
					newRequestHeaders.add(accessTokenName, apimTokenResponse.getAccessToken());
					input.setHeaders(newRequestHeaders);
				}
			} else {
				logger.debug("Did not receive access_token from observable");
			}
			return input;
		});
	}

	@Override
	public boolean shouldFilter(HttpRequestMessage msg) {
		
		boolean apimAuthorizationEnabled = ConfigurationManager.getConfigInstance()
				.getBoolean("com.claro.config.authorization.apim.enabled");
		
		if(apimAuthorizationEnabled) {
			List<Object> urlPrefixes = ConfigurationManager.getConfigInstance()
					.getList("com.claro.config.authorization.apim.urlprefixes", Collections.emptyList());
			String path = msg.getPath();
			boolean matches = urlPrefixes.stream().anyMatch((urlPrefix) -> path.startsWith((String)urlPrefix));
			return matches;
		}
		
		return false;
	}

}
