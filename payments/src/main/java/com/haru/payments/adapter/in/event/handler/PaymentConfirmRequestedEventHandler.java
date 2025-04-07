package com.haru.payments.adapter.in.event.handler;

import com.alert.core.manager.AlertManager;
import com.haru.payments.adapter.in.event.PaymentConfirmRequestedEvent;
import com.haru.payments.adapter.in.event.payload.PaymentConfirmRequestedEventPayload;
import com.haru.payments.adapter.out.alert.CommonAlertChannel;
import com.haru.payments.application.usecase.dto.PaymentConfirmResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmRequestedEventHandler {
    private final ApplicationEventPublisher eventPublisher;
    private final AlertManager alertManager;

    @Transactional
    public void handle(PaymentConfirmRequestedEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            PaymentConfirmRequestedEvent event = PaymentConfirmRequestedEvent.fail(payload.getRequestId(), payload.getRequestMemberId(), payload.getRequestPrice(), payload.getFailureReason());
            alertManager.notice(CommonAlertChannel.PAYMENT_RESULT, payload.getRequestId().toString(), PaymentConfirmResponse.of(event));
            eventPublisher.publishEvent(event);
        } else {
            PaymentConfirmRequestedEvent event = PaymentConfirmRequestedEvent.success(payload.getRequestId(), payload.getRequestMemberId(), payload.getRequestPrice());
            eventPublisher.publishEvent(event);
        }
    }
}
