package com.haru.payments.adapter.in.event.handler;

import com.haru.common.RequiresNewExecutor;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmPaymentRequestEventHandler {
    private final ApplicationEventPublisher eventPublisher;
    private final ConfirmPaymentUseCase paymentConfirmUseCase;
    private final AlertManager alertManager;
    private final RequiresNewExecutor requiresNewExecutor;

    @Transactional
    public void handle(ConfirmPaymentRequestEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            paymentConfirmUseCase.failConfirm(payload.getRequestId());
            eventPublisher.publishEvent(PaymentConfirmedEvent.fail(payload.getRequestId(), payload.getRequestId(), payload.getFailureReason()));
        } else {
            try {
                PaymentConfirmResponse confirmResponse = paymentConfirmUseCase.confirm(new CompletePaymentRequest(payload.getRequestId()));
                alertManager.notice(AlertChannel.PAYMENT_RESULT, payload.getRequestId().toString(), confirmResponse);
                eventPublisher.publishEvent(PaymentConfirmedEvent.success(payload.getRequestId(), payload.getRequestId()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                requiresNewExecutor.execute(() -> eventPublisher.publishEvent(PaymentConfirmedEvent.fail(payload.getRequestId(), payload.getRequestId(), e.getMessage())));
            }
        }
    }
}
