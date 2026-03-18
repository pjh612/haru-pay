package com.haru.money.adapters.in.event.payment.handler;

import com.haru.common.RequiresNewExecutor;
import com.haru.money.adapters.in.event.payment.payload.DecreaseMoneyEventPayload;
import com.haru.money.application.usecase.DecreaseMoneyUseCase;
import com.haru.money.domain.repository.ProcessedEventLogRepository;
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
class DecreaseMoneyEventHandlerTest {
    @InjectMocks
    private DecreaseMoneyEventHandler handler;

    @Mock
    private DecreaseMoneyUseCase decreaseMoneyUseCase;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private RequiresNewExecutor requiresNewExecutor;
    @Mock
    private ProcessedEventLogRepository processedEventLogRepository;

    @Test
    void handle_ShouldSkipDuplicateEvent() {
        UUID eventId = UUID.randomUUID();
        DecreaseMoneyEventPayload payload = new DecreaseMoneyEventPayload(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, "REQUEST");
        when(processedEventLogRepository.markIfFirst("money.decrease-money", eventId)).thenReturn(false);

        handler.handle(eventId, payload);

        verify(decreaseMoneyUseCase, never()).decrease(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(decreaseMoneyUseCase, never()).cancelDecreaseMoneyRequest(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }
}
