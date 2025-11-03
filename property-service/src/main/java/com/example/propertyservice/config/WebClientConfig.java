package com.example.propertyservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${USER_SERVICE_URL}")
    private String USER_SERVICE_URL;

    @Bean
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(USER_SERVICE_URL)
                .build();
    }
}
