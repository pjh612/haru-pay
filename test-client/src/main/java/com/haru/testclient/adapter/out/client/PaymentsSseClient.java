package com.haru.testclient.adapter.out.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
public class PaymentsSseClient {

    private final WebClient webClient;

    public PaymentsSseClient(WebClient paymentsWebClient) {
        this.webClient = paymentsWebClient;
    }

    public Flux<String> subscribeToPaymentResults(String clientId, String apiKey, UUID paymentId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/payment-result/subscribe")
                        .queryParam("paymentId", paymentId.toString())
                        .build())
                .header("Authorization", "apiKey " + apiKey)
                .header("X-PAY-CLIENT-ID", clientId)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
