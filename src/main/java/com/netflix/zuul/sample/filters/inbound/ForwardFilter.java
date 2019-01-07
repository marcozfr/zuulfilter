package com.netflix.zuul.sample.filters.inbound;

import java.util.Collections;
import java.util.List;

import com.netflix.config.ConfigurationManager;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.http.HttpInboundSyncFilter;
import com.netflix.zuul.message.http.HttpRequestMessage;

public class ForwardFilter extends HttpInboundSyncFilter {

    @Override
    public HttpRequestMessage apply(HttpRequestMessage input) {
        SessionContext context = input.getContext();
        @SuppressWarnings("unchecked")
		List<String> forwardUrls = ConfigurationManager
    			.getConfigInstance().getList("com.claro.config.forward.url", Collections.EMPTY_LIST);
        for (String forwardUrl: forwardUrls) {
        	String[] forwardUrlArray = forwardUrl.split(":");
        	String origin = forwardUrlArray[0];
        	String destination = forwardUrlArray[1];
            if (origin.equals(input.getPath())) {
            	context.set("overrideURI", destination);
            }
        }
        return input;
    }

    @Override
    public int filterOrder() {
        return 503;
    }

    @Override
    public boolean shouldFilter(HttpRequestMessage msg) {
    	
    	@SuppressWarnings("unchecked")
		List<String> forwardUrls = ConfigurationManager
    			.getConfigInstance().getList("com.claro.config.forward.url", Collections.EMPTY_LIST);
        for (String forwardUrl: forwardUrls) {
        	String[] forwardUrlArray = forwardUrl.split(":");
        	String origin = forwardUrlArray[0];
            if (origin.equals(msg.getPath())) {
                return true;
            }
        }
        return false;
    }
}
