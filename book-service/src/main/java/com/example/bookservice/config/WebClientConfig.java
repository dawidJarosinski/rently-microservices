package com.example.bookservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${USER_SERVICE_URL}")
    private String USER_SERVICE_URL;

    @Value("${PROPERTY_SERVICE_URL}")
    private String PROPERTY_SERVICE_URL;

    @Bean
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(USER_SERVICE_URL)
                .build();
    }

    @Bean
    public WebClient propertyServiceWebClient() {
        return WebClient.builder()
                .baseUrl(PROPERTY_SERVICE_URL)
                .build();
    }
}
