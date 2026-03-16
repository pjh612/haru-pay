package com.haru.testclient.adapter.out.client;

import com.haru.testclient.application.dto.ConfirmPaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentsCommandClient {

    private final RestClient restClient;

    public PaymentsCommandClient(RestClient paymentsRestClient) {
        this.restClient = paymentsRestClient;
    }

    public PreparePaymentResponse preparePayment(String clientId, String apiKey, PreparePaymentRequest request) {
        return restClient.post()
                .uri("/api/payment/prepare")
                .header("Authorization", "apiKey " + apiKey)
                .header("X-PAY-CLIENT-ID", clientId)
                .body(request)
                .retrieve()
                .body(PreparePaymentResponse.class);
    }

    public void confirmPayment(String clientId, String apiKey, ConfirmPaymentRequest request) {
        restClient.post()
                .uri("/api/payment/confirm")
                .header("Authorization", "apiKey " + apiKey)
                .header("X-PAY-CLIENT-ID", clientId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
