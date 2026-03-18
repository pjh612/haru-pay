package com.haru.testclient.application.service;

import com.haru.testclient.adapter.out.client.PaymentsCommandClient;
import com.haru.testclient.application.dto.ConfirmPaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentRequest;
import com.haru.testclient.application.dto.PreparePaymentResponse;
import com.haru.testclient.domain.model.MerchantSession;
import com.haru.testclient.domain.model.PreparedPayment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MerchantPaymentService {

    private final PaymentsCommandClient commandClient;
    private final Map<String, List<PreparedPayment>> paymentsByClient = new ConcurrentHashMap<>();

    public MerchantPaymentService(PaymentsCommandClient commandClient) {
        this.commandClient = commandClient;
    }

    public PreparedPayment preparePayment(MerchantSession session, String orderId, String productName, BigDecimal requestPrice, String idempotencyKey) {
        PreparePaymentRequest request = new PreparePaymentRequest(orderId, productName, requestPrice);
        
        PreparePaymentResponse response = commandClient.preparePayment(
                session.getClientId().toString(),
                session.getApiKey(),
                request,
                idempotencyKey
        );
        
        PreparedPayment payment = new PreparedPayment(
                response.paymentId(),
                orderId,
                productName,
                requestPrice,
                Instant.now(),
                "PREPARED"
        );
        
        paymentsByClient
                .computeIfAbsent(session.getClientId().toString(), k -> new ArrayList<>())
                .add(payment);
        
        return payment;
    }

    public void confirmPayment(MerchantSession session, UUID paymentId, String idempotencyKey) {
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(paymentId);
        commandClient.confirmPayment(
                session.getClientId().toString(),
                session.getApiKey(),
                request,
                idempotencyKey
        );
        
        updatePaymentStatus(session.getClientId().toString(), paymentId, "CONFIRMING");
    }

    public PreparedPayment confirmPreparedPayment(MerchantSession session, UUID paymentId, String orderId, BigDecimal requestPrice) {
        PreparedPayment payment = getRequiredPayment(session.getClientId().toString(), paymentId);

        if (!Objects.equals(payment.getOrderId(), orderId)) {
            throw new IllegalArgumentException("주문 정보가 일치하지 않습니다.");
        }

        if (payment.getRequestPrice().compareTo(requestPrice) != 0) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        if ("SUCCEEDED".equals(payment.getStatus()) || "CONFIRMING".equals(payment.getStatus())) {
            return payment;
        }

        if ("FAILED".equals(payment.getStatus())) {
            throw new IllegalStateException("이미 실패한 결제입니다.");
        }

        confirmPayment(session, paymentId, null);
        payment.setStatus("CONFIRMING");
        return payment;
    }

    public List<PreparedPayment> getPayments(String clientId) {
        return paymentsByClient.getOrDefault(clientId, new ArrayList<>());
    }

    public void updatePaymentStatus(String clientId, UUID paymentId, String status) {
        List<PreparedPayment> payments = paymentsByClient.get(clientId);
        if (payments != null) {
            payments.stream()
                    .filter(p -> p.getPaymentId().equals(paymentId))
                    .findFirst()
                    .ifPresent(p -> p.setStatus(status));
        }
    }

    private PreparedPayment getRequiredPayment(String clientId, UUID paymentId) {
        return paymentsByClient.getOrDefault(clientId, List.of())
                .stream()
                .filter(payment -> payment.getPaymentId().equals(paymentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));
    }
}
