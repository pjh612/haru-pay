package com.haru.payments.adapter.in.event.handler;

import com.alert.core.manager.AlertManager;
import com.haru.payments.adapter.in.event.PaymentConfirmRequestedEvent;
import com.haru.payments.adapter.in.event.payload.PaymentConfirmRequestedEventPayload;
import com.haru.payments.domain.repository.ProcessedEventLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void handle_ShouldPublishSucceededEvent_WhenRequestTypeIsRequest() {
        UUID eventId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        PaymentConfirmRequestedEventPayload payload = new PaymentConfirmRequestedEventPayload(requestId, memberId, BigDecimal.TEN, null, "REQUEST");
        when(processedEventLogRepository.markIfFirst("payments.payment-confirm-requested", eventId)).thenReturn(true);

        handler.handle(eventId, payload);

        ArgumentCaptor<PaymentConfirmRequestedEvent> captor = ArgumentCaptor.forClass(PaymentConfirmRequestedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        PaymentConfirmRequestedEvent published = captor.getValue();
        assertThat(published.getType()).isEqualTo("SUCCEEDED");
        assertThat(published.getRequestId()).isEqualTo(requestId);
        verify(alertManager, never()).notice(org.mockito.ArgumentMatchers.any(), anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void handle_ShouldPublishFailedEventAndNotice_WhenRequestTypeIsCancel() {
        UUID eventId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        PaymentConfirmRequestedEventPayload payload = new PaymentConfirmRequestedEventPayload(requestId, memberId, BigDecimal.TEN, "cancelled", "CANCEL");
        when(processedEventLogRepository.markIfFirst("payments.payment-confirm-requested", eventId)).thenReturn(true);

        handler.handle(eventId, payload);

        ArgumentCaptor<PaymentConfirmRequestedEvent> captor = ArgumentCaptor.forClass(PaymentConfirmRequestedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        PaymentConfirmRequestedEvent published = captor.getValue();
        assertThat(published.getType()).isEqualTo("FAILED");
        assertThat(published.getFailureReason()).isEqualTo("cancelled");
        verify(alertManager).notice(org.mockito.ArgumentMatchers.any(), anyString(), org.mockito.ArgumentMatchers.any());
    }
}
