package com.kds.boot.reactive.reactiverest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "shop")
public class EndpointConfig {

    private String endpoint;
}
