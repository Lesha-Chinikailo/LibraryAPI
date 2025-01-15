package com.java.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final String BEARER_TOKEN_PREFIX = "Bearer ";
    private final RouteValidator routeValidator;
    private final RestTemplate restTemplate;
    private final Properties configProperties;

    @Autowired
    public AuthenticationFilter(RouteValidator routeValidator,
                                RestTemplate restTemplate,
                                @Qualifier("configProperties") Properties configProperties) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.restTemplate = restTemplate;
        this.configProperties = configProperties;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Authorization header not present");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                if(authHeader != null && authHeader.startsWith(BEARER_TOKEN_PREFIX)) {
                    authHeader = authHeader.substring(BEARER_TOKEN_PREFIX.length());
                }
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Content-Type", "application/json");
                    headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                    String urlValidateToken = configProperties.getProperty("url.validateToken");
                    ResponseEntity<Object> response = restTemplate.exchange(
                            urlValidateToken,
                            HttpMethod.GET,
                            requestEntity,
                            Object.class
                    );

                    System.out.println("\n\n\n\n" + response + "\n\n\n\n");

                    if(!response.getStatusCode().is2xxSuccessful()) {
                        throw new RuntimeException("Invalid token");
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            return chain.filter(exchange);
        };
    }

    public static class Config{

    }
}
