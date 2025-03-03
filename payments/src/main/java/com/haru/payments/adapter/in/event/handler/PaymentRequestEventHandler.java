package com.haru.payments.adapter.in.event.handler;

import com.haru.payments.adapter.in.event.PaymentRequestCreatedEvent;
import com.haru.payments.adapter.in.event.payload.CreatePaymentRequestEventPayload;
import com.haru.payments.application.dto.CreatePaymentRequest;
import com.haru.payments.application.usecase.RequestPaymentUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestEventHandler {
    private final ApplicationEventPublisher eventPublisher;
    private final RequestPaymentUseCase requestPaymentUseCase;

    @Transactional
    public void handle(UUID sagaId, CreatePaymentRequestEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            requestPaymentUseCase.failRequest(payload.getRequestId());
            publishFailEvent(sagaId, payload);
        } else {
            try {
                requestPaymentUseCase.requestPayment(new CreatePaymentRequest(
                        payload.getRequestId(),
                        payload.getRequestMemberId(),
                        payload.getRequestPrice(),
                        payload.getSellerId()));
                publishSuccessEvent(sagaId, payload);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                publishFailEvent(sagaId, payload);
            }
        }
    }

    private void publishSuccessEvent(UUID sagaId, CreatePaymentRequestEventPayload payload) {
        eventPublisher.publishEvent(PaymentRequestCreatedEvent.success(
                sagaId,
                payload.getRequestId(),
                payload.getSellerId(),
                payload.getRequestMemberId(),
                payload.getRequestPrice()));
    }

    private void publishFailEvent(UUID sagaId, CreatePaymentRequestEventPayload payload) {
        eventPublisher.publishEvent(PaymentRequestCreatedEvent.fail(
                sagaId,
                null,
                payload.getSellerId(),
                payload.getRequestMemberId(),
                payload.getRequestPrice()));
    }
}
