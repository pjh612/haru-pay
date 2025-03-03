package com.haru.payments.adapter.out.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean("memberRestClient")
    public RestClient memberRestClient(RestClient.Builder builder, @Value("${client.member.url}") String url) {
        return builder.baseUrl(url)
                .build();
    }

    @Bean("bankingRestClient")
    public RestClient bankingRestClient(RestClient.Builder builder, @Value("${client.banking.url}") String url) {
        return builder.baseUrl(url)
                .build();
    }

    @Bean("moneyRestClient")
    public RestClient moneyRestClient(RestClient.Builder builder, @Value("${client.money.url}") String url) {
        return builder.baseUrl(url)
                .build();
    }
}
