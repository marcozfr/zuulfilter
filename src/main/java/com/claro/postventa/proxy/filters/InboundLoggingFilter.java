package com.claro.postventa.proxy.filters;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.claro.postventa.proxy.util.LogConstants;
import com.google.gson.Gson;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.http.HttpInboundSyncFilter;
import com.netflix.zuul.message.ZuulMessage;
import com.netflix.zuul.message.http.HttpRequestMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;

public class InboundLoggingFilter extends HttpInboundSyncFilter {
	
	public static final Logger logger = LoggerFactory.getLogger(InboundLoggingFilter.class);
	
	private Gson gson;
	
	public InboundLoggingFilter() {
		gson = new Gson();
	}
	
	@Override
	public boolean needsBodyBuffered(HttpRequestMessage input) {
		return true;
	}

	@Override
	public HttpRequestMessage apply(HttpRequestMessage input) {
		
		SessionContext context = input.getContext();
		
		logger.info(LogConstants.LOG_REQUEST_HEADER, 
					context.get("overrideURI"), 
					gson.toJson(input.getHeaders().entries()));
		logger.info(LogConstants.LOG_REQUEST_BODY, 
					context.get("overrideURI"), 
					input.getBodyAsText());
		
		context.set("startTime", Instant.now());
		
		return input;
	}

	@Override
	public int filterOrder() {
		return 504;
	}

	@Override
	public boolean shouldFilter(HttpRequestMessage msg) {
		return true;
	}


}
