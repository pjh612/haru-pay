package com.haru.money.adapters.out.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {
    @Bean("bankingClient")
    RestClient bankingClient(RestClient.Builder builder, @Value("${client.banking.url}") String url) {
        return builder.baseUrl(url).build();
    }
}
