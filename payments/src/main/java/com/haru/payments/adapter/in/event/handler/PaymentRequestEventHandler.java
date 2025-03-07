package com.haru.payments.adapter.in.event.handler;

import com.haru.payments.adapter.in.event.payload.CreatePaymentRequestEventPayload;
import com.haru.payments.application.dto.CreatePaymentRequest;
import com.haru.payments.application.usecase.RequestPaymentUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestEventHandler {
    private final RequestPaymentUseCase requestPaymentUseCase;

    @Transactional
    public void handle(UUID sagaId, CreatePaymentRequestEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            requestPaymentUseCase.failRequest(payload.getRequestId());
        } else {
            try {
                requestPaymentUseCase.requestPayment(new CreatePaymentRequest(
                        payload.getRequestId(),
                        payload.getOrderId(),
                        payload.getRequestMemberId(),
                        payload.getProductName(),
                        payload.getRequestPrice(),
                        payload.getClientId()));
            } catch (Exception e) {
                requestPaymentUseCase.failRequest(payload.getRequestId());
            }
        }
    }
}
