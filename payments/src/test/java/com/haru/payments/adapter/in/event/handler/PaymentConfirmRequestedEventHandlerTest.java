package com.haru.payments.adapter.in.event.handler;

import com.alert.core.manager.AlertManager;
import com.haru.payments.adapter.in.event.payload.PaymentConfirmRequestedEventPayload;
import com.haru.payments.domain.repository.ProcessedEventLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentConfirmRequestedEventHandlerTest {
    @InjectMocks
    private PaymentConfirmRequestedEventHandler handler;

    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private AlertManager alertManager;
    @Mock
    private ProcessedEventLogRepository processedEventLogRepository;

    @Test
    void handle_ShouldSkipDuplicateEvent() {
        UUID eventId = UUID.randomUUID();
        PaymentConfirmRequestedEventPayload payload = new PaymentConfirmRequestedEventPayload(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, "REQUEST");
        when(processedEventLogRepository.markIfFirst("payments.payment-confirm-requested", eventId)).thenReturn(false);

        handler.handle(eventId, payload);

        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
        verify(alertManager, never()).notice(org.mockito.ArgumentMatchers.any(), anyString(), org.mockito.ArgumentMatchers.any());
    }
}
