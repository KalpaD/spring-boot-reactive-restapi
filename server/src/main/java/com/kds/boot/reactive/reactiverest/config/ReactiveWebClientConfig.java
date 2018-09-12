package com.kds.boot.reactive.reactiverest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ReactiveWebClientConfig {

    private EndpointConfig endpointConfig;

    public ReactiveWebClientConfig(EndpointConfig endpointConfig) {
        this.endpointConfig = endpointConfig;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(endpointConfig.getEndpoint())
                .build();
    }
}
