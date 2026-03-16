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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MerchantPaymentService {

    private final PaymentsCommandClient commandClient;
    private final Map<String, List<PreparedPayment>> paymentsByClient = new ConcurrentHashMap<>();

    public MerchantPaymentService(PaymentsCommandClient commandClient) {
        this.commandClient = commandClient;
    }

    public PreparedPayment preparePayment(MerchantSession session, String orderId, String productName, BigDecimal requestPrice) {
        PreparePaymentRequest request = new PreparePaymentRequest(orderId, productName, requestPrice);
        
        PreparePaymentResponse response = commandClient.preparePayment(
                session.getClientId().toString(),
                session.getApiKey(),
                request
        );
        
        PreparedPayment payment = new PreparedPayment(
                response.requestId(),
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

    public void confirmPayment(MerchantSession session, UUID paymentId) {
        ConfirmPaymentRequest request = new ConfirmPaymentRequest(paymentId);
        commandClient.confirmPayment(
                session.getClientId().toString(),
                session.getApiKey(),
                request
        );
        
        updatePaymentStatus(session.getClientId().toString(), paymentId, "CONFIRMING");
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
}
