package com.haru.banking.adapter.out.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean("memberRestClient")
    RestClient memberRestClient(RestClient.Builder builder, @Value("${client.member.url}") String url) {
        return builder.baseUrl(url)
                .build();
    }
}
