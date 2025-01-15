package com.java.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "configProperties")
    public Properties getConfigProperties() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("configuration.properties")) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
