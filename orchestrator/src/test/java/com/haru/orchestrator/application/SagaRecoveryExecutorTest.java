package com.haru.orchestrator.application;

import com.haru.orchestrator.domain.model.PayloadType;
import com.haru.orchestrator.domain.model.RecoveryStatus;
import com.haru.orchestrator.domain.model.SagaState;
import com.haru.orchestrator.domain.model.SagaStatus;
import com.haru.orchestrator.domain.repository.SagaStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaRecoveryExecutorTest {
    @Mock
    private SagaStateRepository sagaStateRepository;
    @Mock
    private SagaEventPublisher sagaEventPublisher;

    private SagaRecoveryExecutor executor;
    private final JsonMapper jsonMapper = new JsonMapper();

    @BeforeEach
    void setUp() {
        executor = new SagaRecoveryExecutor(sagaStateRepository, sagaEventPublisher,
                Clock.fixed(Instant.parse("2026-03-18T00:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void redrive_shouldPublishEventAndIncrementRecoveryAttempt() {
        SagaState state = buildState();
        when(sagaStateRepository.findById(state.getId())).thenReturn(Optional.of(state));

        executor.redrive(state.getId(), PayloadType.REQUEST, 3);

        verify(sagaEventPublisher).publishEvent(any());
        ArgumentCaptor<SagaState> captor = ArgumentCaptor.forClass(SagaState.class);
        verify(sagaStateRepository).save(captor.capture());
        SagaState savedState = captor.getValue();

        assertThat(savedState.getRecoveryAttemptCount()).isEqualTo(1);
        assertThat(savedState.getRecoveryStatus()).isEqualTo(RecoveryStatus.RETRYING);
        assertThat(savedState.getLastRecoveryAt()).isEqualTo(Instant.parse("2026-03-18T00:00:00Z"));
    }

    @Test
    void redrive_shouldMarkManualReviewWhenAttemptsExceeded() {
        SagaState state = buildState();
        state.markRecoveryAttempt(Instant.parse("2026-03-17T23:59:00Z"));
        when(sagaStateRepository.findById(state.getId())).thenReturn(Optional.of(state));

        executor.redrive(state.getId(), PayloadType.REQUEST, 1);

        verify(sagaEventPublisher, never()).publishEvent(any());
        ArgumentCaptor<SagaState> captor = ArgumentCaptor.forClass(SagaState.class);
        verify(sagaStateRepository).save(captor.capture());
        assertThat(captor.getValue().getRecoveryStatus()).isEqualTo(RecoveryStatus.MANUAL_REVIEW_REQUIRED);
    }

    @Test
    void markManualReview_shouldPersistManualReviewStatus() {
        SagaState state = buildState();
        when(sagaStateRepository.findById(state.getId())).thenReturn(Optional.of(state));

        executor.markManualReview(state.getId(), "unsafe step");

        ArgumentCaptor<SagaState> captor = ArgumentCaptor.forClass(SagaState.class);
        verify(sagaStateRepository).save(captor.capture());
        assertThat(captor.getValue().getRecoveryStatus()).isEqualTo(RecoveryStatus.MANUAL_REVIEW_REQUIRED);
    }

    private SagaState buildState() {
        SagaState state = new SagaState(UUID.randomUUID(), "PAYMENT_SAGA", jsonMapper.createObjectNode());
        state.updateCurrentStep("payment-confirm-request");
        state.updateCurrentPayload(jsonMapper.createObjectNode().put("requestId", UUID.randomUUID().toString()));
        state.updateStepStatus("payment-confirm-request", com.haru.orchestrator.domain.model.SagaStepStatus.STARTED);
        state.markProgress(Instant.parse("2026-03-17T23:55:00Z"));
        assertThat(state.getSagaStatus()).isEqualTo(SagaStatus.STARTED);
        return state;
    }
}
