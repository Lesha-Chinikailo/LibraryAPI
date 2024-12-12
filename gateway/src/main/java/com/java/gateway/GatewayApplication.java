package com.java.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.contract.stubrunner.server.EnableStubRunnerServer;
//import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableZuulProxy
//@EnableStubRunnerServer
public class GatewayApplication {

    public static void main(String[] args) {
//        new SpringApplicationBuilder(GatewayApplication.class)
//                .web(WebApplicationType.REACTIVE)
//                .run(args);
        SpringApplication.run(GatewayApplication.class, args);
    }

}
