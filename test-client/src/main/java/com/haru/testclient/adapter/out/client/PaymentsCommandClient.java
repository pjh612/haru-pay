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

    public PreparePaymentResponse preparePayment(String clientId, String apiKey, PreparePaymentRequest request, String idempotencyKey) {
        var requestSpec = restClient.post()
                .uri("/api/payment/prepare")
                .header("Authorization", apiKey)
                .header("X-PAY-CLIENT-ID", clientId);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            requestSpec.header("Idempotency-Key", idempotencyKey);
        }

        return requestSpec
                .body(request)
                .retrieve()
                .body(PreparePaymentResponse.class);
    }

    public void confirmPayment(String clientId, String apiKey, ConfirmPaymentRequest request, String idempotencyKey) {
        var requestSpec = restClient.post()
                .uri("/api/payment/confirm")
                .header("Authorization", apiKey)
                .header("X-PAY-CLIENT-ID", clientId);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            requestSpec.header("Idempotency-Key", idempotencyKey);
        }

        requestSpec
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
