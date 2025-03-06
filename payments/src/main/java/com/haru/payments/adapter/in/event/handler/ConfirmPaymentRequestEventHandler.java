package com.haru.payments.adapter.in.event.handler;

import com.haru.payments.adapter.in.event.PaymentConfirmedEvent;
import com.haru.payments.adapter.in.event.payload.ConfirmPaymentRequestEventPayload;
import com.haru.payments.application.dto.CompletePaymentRequest;
import com.haru.payments.application.usecase.ConfirmPaymentUseCase;
import com.haru.payments.application.usecase.dto.PaymentConfirmResponse;
import com.haru.payments.common.alert.AlertChannel;
import com.haru.payments.common.alert.AlertManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmPaymentRequestEventHandler {
    private final ApplicationEventPublisher eventPublisher;
    private final ConfirmPaymentUseCase paymentConfirmUseCase;
    private final AlertManager alarmManager;

    @Transactional
    public void handle(UUID sagaId, ConfirmPaymentRequestEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            paymentConfirmUseCase.failConfirm(payload.getRequestId());
            eventPublisher.publishEvent(PaymentConfirmedEvent.fail(sagaId, payload.getRequestId()));
        } else {
            try {
                PaymentConfirmResponse confirmResponse = paymentConfirmUseCase.confirm(new CompletePaymentRequest(payload.getRequestId()));
                alarmManager.notice(AlertChannel.PAYMENT_RESULT, payload.getRequestId().toString(), confirmResponse);
                eventPublisher.publishEvent(PaymentConfirmedEvent.success(sagaId, payload.getRequestId()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                eventPublisher.publishEvent(PaymentConfirmedEvent.fail(sagaId, payload.getRequestId()));
            }
        }
    }
}
