/*
 * Copyright 2018 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.claro.postventa.proxy;

import com.claro.postventa.proxy.configuration.DynamoDBConfigurationSource;
import com.google.inject.Injector;
import com.netflix.config.AbstractPollingScheduler;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PolledConfigurationSource;
import com.netflix.governator.InjectorBuilder;
import com.netflix.zuul.netty.server.BaseServerStartup;
import com.netflix.zuul.netty.server.Server;

/**
 * Bootstrap
 *
 * Author: Arthur Gonigberg
 * Date: November 20, 2017
 */
public class Bootstrap {

    public static void main(String[] args) {
        new Bootstrap().start();
    }

    
    public void start() {
        System.out.println("Zuul Sample: starting up.");
        long startTime = System.currentTimeMillis();
        int exitCode = 0;

        Server server = null;

        try {
        	
//        	PolledConfigurationSource dynamoConfigurationSource = new DynamoDBConfigurationSource();
//        	AbstractPollingScheduler fixedDelayPollingScheduler = new FixedDelayPollingScheduler(); 
//        	DynamicConfiguration configuration = new DynamicConfiguration(dynamoConfigurationSource, fixedDelayPollingScheduler);
//        	ConfigurationManager.install(configuration);
        	
            ConfigurationManager.loadCascadedPropertiesFromResources("application");
        	
            Injector injector = InjectorBuilder.fromModule(new ZuulSampleModule()).createInjector();
            injector.getInstance(FiltersRegisteringService.class);
            BaseServerStartup serverStartup =  injector.getInstance(BaseServerStartup.class);
            server = serverStartup.server();

            long startupDuration = System.currentTimeMillis() - startTime;
            System.out.println("Zuul Sample: finished startup. Duration = " + startupDuration + " ms");

            server.start(true);
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.err.println("###############");
            System.err.println("Zuul Sample: initialization failed. Forcing shutdown now.");
            System.err.println("###############");
            exitCode = 1;
        }
        finally {
            // server shutdown
            if (server != null) server.stop();

            System.exit(exitCode);
        }
    }
}
