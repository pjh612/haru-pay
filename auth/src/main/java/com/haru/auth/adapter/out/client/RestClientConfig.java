package com.haru.auth.adapter.out.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    RestClient restClient(RestClient.Builder builder, @Value("${client.user.url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }
}
