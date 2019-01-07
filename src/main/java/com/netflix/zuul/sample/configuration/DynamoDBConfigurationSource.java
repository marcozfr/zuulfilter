package com.netflix.zuul.sample.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;
import com.netflix.zuul.sample.to.ForwardUrl;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

public class DynamoDBConfigurationSource implements PolledConfigurationSource {
	
	public static final Logger logger = LoggerFactory.getLogger(DynamoDBConfigurationSource.class);
	
	private DynamoDbClient dynamoDbClient;

	public DynamoDBConfigurationSource() {
		Region region = Region.US_EAST_1;
		dynamoDbClient = DynamoDbClient.builder().region(region).build();
	}

	@Override
	public PollResult poll(boolean initial, Object checkPoint) throws Exception {
		
		logger.info("Reloading properties from source ");
		
		Map<String,Object> properties = new java.util.HashMap<>();
		
		ScanRequest scanRequest = ScanRequest.builder()
				.tableName("ZuulConfiguration")
				.build();
		ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
		
		for(Map<String,AttributeValue> item : scanResponse.items()) {
			AttributeValue propertyKeyAttribute = item.get("PropertyKey");
			AttributeValue propertyValueAttribute = item.get("PropertyValue");
			String propertyKey = propertyKeyAttribute.s();
			final String forwardUrlKey = "com.claro.config.forward.url";
			if(forwardUrlKey.equals(propertyKey)) {
				List<AttributeValue> urlMappingValues = propertyValueAttribute.l();
				if(urlMappingValues!=null && !urlMappingValues.isEmpty()) {
					StringBuilder forwardUrlBuilder = new StringBuilder();
					urlMappingValues.stream().map((urlMapping)-> urlMapping.m()).forEach((map) -> {
						String origin = map.get("origin").s();
						String destination = map.get("destination").s();
						if(forwardUrlBuilder.length() != 0) {
							forwardUrlBuilder.append(",");
						}
						forwardUrlBuilder.append(origin).append(":").append(destination);
					});
					properties.put(forwardUrlKey, forwardUrlBuilder.toString());
				}
			} else {
				properties.put(propertyKey, propertyValueAttribute.s());
			}
		}
		
		return PollResult.createFull(properties);
	}

}
