package com.netflix.zuul.sample.filters.inbound;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.zuul.message.http.HttpRequestMessage;

public class ForwardFilterTest {
    
//    @Test
    public void testShouldFilter() {
//        ForwardFilter filter = new ForwardFilter(new ProxyConfigurationService(executorService, proxyConfigurationDAO));
//        assertThat(filter.shouldFilter(sampleHttpMessage("/path1/testRemaining?query1=val1"))).isTrue();
//        assertThat(filter.shouldFilter(sampleHttpMessage("/path2/testRemaining?query1=val1"))).isFalse();
    }

    private HttpRequestMessage sampleHttpMessage(String path) {
        HttpRequestMessage sampleRequest = mock(HttpRequestMessage.class);
        when(sampleRequest.getPath()).thenReturn(path);
        return sampleRequest;
    }
}
