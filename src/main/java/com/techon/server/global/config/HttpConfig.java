package com.techon.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpConfig {
    @Bean
    public WebClient defaultWebClient(){
        return WebClient.builder().build();
    }
}
