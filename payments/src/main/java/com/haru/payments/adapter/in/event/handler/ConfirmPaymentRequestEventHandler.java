package com.haru.payments.adapter.in.event.handler;

import com.alert.core.manager.AlertManager;
import com.haru.common.RequiresNewExecutor;
import com.haru.payments.adapter.in.event.PaymentConfirmedEvent;
import com.haru.payments.adapter.in.event.payload.ConfirmPaymentRequestEventPayload;
import com.haru.payments.adapter.out.alert.CommonAlertChannel;
import com.haru.payments.application.dto.CompletePaymentRequest;
import com.haru.payments.application.usecase.ConfirmPaymentUseCase;
import com.haru.payments.application.dto.PaymentConfirmResponse;
import com.haru.payments.domain.repository.ProcessedEventLogRepository;
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
    private static final String CONSUMER_KEY = "payments.confirm-payment-request";
    private final ApplicationEventPublisher eventPublisher;
    private final ConfirmPaymentUseCase paymentConfirmUseCase;
    private final AlertManager alertManager;
    private final RequiresNewExecutor requiresNewExecutor;
    private final ProcessedEventLogRepository processedEventLogRepository;

    @Transactional
    public void handle(UUID eventId, ConfirmPaymentRequestEventPayload payload) {
        if (!processedEventLogRepository.markIfFirst(CONSUMER_KEY, eventId)) {
            log.info("Duplicate confirm-payment event skipped. eventId={}", eventId);
            return;
        }

        if ("CANCEL".equals(payload.getType())) {
            paymentConfirmUseCase.failConfirm(payload.getRequestId());
            eventPublisher.publishEvent(PaymentConfirmedEvent.fail(payload.getRequestId(), payload.getRequestId(), payload.getFailureReason()));
        } else {
            try {
                PaymentConfirmResponse confirmResponse = paymentConfirmUseCase.confirm(new CompletePaymentRequest(payload.getRequestId()));
                alertManager.notice(CommonAlertChannel.PAYMENT_RESULT, payload.getRequestId().toString(), confirmResponse);
                eventPublisher.publishEvent(PaymentConfirmedEvent.success(payload.getRequestId(), payload.getRequestId()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                requiresNewExecutor.execute(() -> eventPublisher.publishEvent(PaymentConfirmedEvent.fail(payload.getRequestId(), payload.getRequestId(), e.getMessage())));
            }
        }
    }
}
