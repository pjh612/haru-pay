package com.haru.testclient.adapter.out.client;

import com.haru.testclient.application.dto.ConfirmPaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentResponse;
import com.haru.testclient.application.exception.PaymentsApiException;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
public class PaymentsCommandClient {

    private final RestClient restClient;
    private final JsonParser jsonParser = JsonParserFactory.getJsonParser();

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

        try {
            return requestSpec
                    .body(request)
                    .retrieve()
                    .body(PreparePaymentResponse.class);
        } catch (RestClientResponseException e) {
            throw toPaymentsApiException(e, "prepare-request-failed", "Prepare Payment Failed", "가결제 요청에 실패했습니다.");
        } catch (RestClientException e) {
            throw new PaymentsApiException(502, "payments-upstream-unavailable", null, "Payments Upstream Unavailable", "결제 서비스와 통신할 수 없습니다.");
        }
    }

    public void confirmPayment(String clientId, String apiKey, ConfirmPaymentRequest request, String idempotencyKey) {
        var requestSpec = restClient.post()
                .uri("/api/payment/confirm")
                .header("Authorization", apiKey)
                .header("X-PAY-CLIENT-ID", clientId);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            requestSpec.header("Idempotency-Key", idempotencyKey);
        }

        try {
            requestSpec
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw toPaymentsApiException(e, "confirm-request-failed", "Confirm Payment Failed", "결제 확정 요청에 실패했습니다.");
        } catch (RestClientException e) {
            throw new PaymentsApiException(502, "payments-upstream-unavailable", null, "Payments Upstream Unavailable", "결제 서비스와 통신할 수 없습니다.");
        }
    }

    private PaymentsApiException toPaymentsApiException(RestClientResponseException e,
                                                        String fallbackType,
                                                        String fallbackTitle,
                                                        String fallbackDetail) {
        String typeUri = null;
        String errorType = fallbackType;
        String title = fallbackTitle;
        String detail = fallbackDetail;

        Map<String, Object> root = parseProblemBody(e.getResponseBodyAsString());
        if (root != null) {
            typeUri = text(root, "type");
            String parsedErrorType = text(root, "errorType");
            if (parsedErrorType != null && !parsedErrorType.isBlank()) {
                errorType = parsedErrorType;
            } else if (typeUri != null && !typeUri.isBlank()) {
                errorType = extractTypeTail(typeUri);
            }

            String parsedTitle = text(root, "title");
            if (parsedTitle != null && !parsedTitle.isBlank()) {
                title = parsedTitle;
            }

            String parsedDetail = text(root, "detail");
            if (parsedDetail != null && !parsedDetail.isBlank()) {
                detail = parsedDetail;
            }
        }

        if ((detail == null || detail.isBlank()) && e.getStatusText() != null && !e.getStatusText().isBlank()) {
            detail = e.getStatusText();
        }

        return new PaymentsApiException(e.getStatusCode().value(), errorType, typeUri, title, detail);
    }

    private String text(Map<String, Object> root, String fieldName) {
        Object value = root.get(fieldName);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    private String extractTypeTail(String typeUri) {
        String[] segments = typeUri.split("/");
        for (int i = segments.length - 1; i >= 0; i--) {
            if (!segments[i].isBlank()) {
                return segments[i];
            }
        }
        return typeUri;
    }

    private Map<String, Object> parseProblemBody(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            return null;
        }

        try {
            return jsonParser.parseMap(rawBody);
        } catch (Exception e) {
            return null;
        }
    }
}
