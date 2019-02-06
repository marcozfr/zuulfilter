package com.claro.postventa.proxy.filters;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.claro.postventa.proxy.util.LogConstants;
import com.google.gson.Gson;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.http.HttpOutboundSyncFilter;
import com.netflix.zuul.message.http.HttpResponseMessage;

public class OutboundLoggingFilter extends HttpOutboundSyncFilter {
	
	public static final Logger logger = LoggerFactory.getLogger(OutboundLoggingFilter.class);
	
	private Gson gson;
	
	public OutboundLoggingFilter() {
		gson = new Gson();
	}
	
	@Override
	public boolean needsBodyBuffered(HttpResponseMessage input) {
		return true;
	}
	
	@Override
	public HttpResponseMessage apply(HttpResponseMessage output) {
		SessionContext context = output.getContext();
		String url = (String) context.get("overrideURI");
		
		Instant startTime = (Instant)context.get("startTime");
		if(startTime!=null) {
			Instant endTime = Instant.now();
			long durationMillis = Duration.between(startTime, endTime).toMillis();
			
			logger.info(LogConstants.LOG_TIEMPOS_PROCESO, 
					url, 
					durationMillis);
		}
		
		logger.info(LogConstants.LOG_RESPONSE_CONTROLADOR, 
				url, 
				output.getBodyLength());
		logger.info(LogConstants.LOG_FIN_CONTROLADOR, 
				url);
		
		return output;
	}

	@Override
	public int filterOrder() {
		return 504;
	}

	@Override
	public boolean shouldFilter(HttpResponseMessage msg) {
		return true;
	}

}
