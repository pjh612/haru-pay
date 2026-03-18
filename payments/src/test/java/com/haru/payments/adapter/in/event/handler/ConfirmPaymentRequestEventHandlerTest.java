package com.haru.payments.adapter.in.event.handler;

import com.alert.core.manager.AlertManager;
import com.haru.common.RequiresNewExecutor;
import com.haru.payments.adapter.in.event.payload.ConfirmPaymentRequestEventPayload;
import com.haru.payments.application.usecase.ConfirmPaymentUseCase;
import com.haru.payments.domain.repository.ProcessedEventLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmPaymentRequestEventHandlerTest {
    @InjectMocks
    private ConfirmPaymentRequestEventHandler handler;

    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ConfirmPaymentUseCase paymentConfirmUseCase;
    @Mock
    private AlertManager alertManager;
    @Mock
    private RequiresNewExecutor requiresNewExecutor;
    @Mock
    private ProcessedEventLogRepository processedEventLogRepository;

    @Test
    void handle_ShouldSkipDuplicateEvent() {
        UUID eventId = UUID.randomUUID();
        ConfirmPaymentRequestEventPayload payload = new ConfirmPaymentRequestEventPayload(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, null, "REQUEST");
        when(processedEventLogRepository.markIfFirst("payments.confirm-payment-request", eventId)).thenReturn(false);

        handler.handle(eventId, payload);

        verify(paymentConfirmUseCase, never()).confirm(org.mockito.ArgumentMatchers.any());
        verify(paymentConfirmUseCase, never()).failConfirm(org.mockito.ArgumentMatchers.any());
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }
}
