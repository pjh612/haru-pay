package com.haru.testclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class PaymentsClientConfig {

    @Value("${payments.base-url}")
    private String paymentsBaseUrl;

    @Value("${payments.connect-timeout:3s}")
    private Duration connectTimeout;

    @Value("${payments.read-timeout:10s}")
    private Duration readTimeout;

    @Bean
    public RestClient paymentsRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(paymentsBaseUrl)
                .requestFactory(factory)
                .build();
    }

    @Bean
    public WebClient paymentsWebClient() {
        return WebClient.builder()
                .baseUrl(paymentsBaseUrl)
                .build();
    }
}
