package com.haru.orchestrator.application;

import com.haru.orchestrator.domain.model.PayloadType;
import com.haru.orchestrator.domain.model.RecoveryStatus;
import com.haru.orchestrator.domain.model.SagaEvent;
import com.haru.orchestrator.domain.model.SagaState;
import com.haru.orchestrator.domain.repository.SagaStateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaRecoveryExecutor {
    private final SagaStateRepository sagaStateRepository;
    private final SagaEventPublisher sagaEventPublisher;
    private final Clock clock;

    @Transactional
    public void redrive(UUID sagaId, PayloadType payloadType, int maxRecoveryAttempts) {
        SagaState state = sagaStateRepository.findById(sagaId)
                .orElse(null);
        if (state == null || state.isTerminal()) {
            return;
        }

        if (state.getRecoveryStatus() == RecoveryStatus.MANUAL_REVIEW_REQUIRED) {
            return;
        }

        if (state.getCurrentStep() == null || state.getCurrentPayload() == null) {
            markManualReviewRequired(state, "Saga recovery skipped because current step payload is missing.");
            return;
        }

        if (state.getRecoveryAttemptCount() >= maxRecoveryAttempts) {
            markManualReviewRequired(state, "Saga recovery exceeded max attempts.");
            return;
        }

        Instant now = Instant.now(clock);
        sagaEventPublisher.publishEvent(new SagaEvent(state.getId(), state.getCurrentStep(), payloadType.name(), state.getCurrentPayload()));
        state.markRecoveryAttempt(now);
        sagaStateRepository.save(state);

        log.warn("Redrove stale saga. sagaId={}, currentStep={}, eventType={}, attempts={}",
                state.getId(), state.getCurrentStep(), payloadType, state.getRecoveryAttemptCount());
    }

    @Transactional
    public void markManualReview(UUID sagaId, String reason) {
        SagaState state = sagaStateRepository.findById(sagaId)
                .orElse(null);
        if (state == null || state.isTerminal() || state.getRecoveryStatus() == RecoveryStatus.MANUAL_REVIEW_REQUIRED) {
            return;
        }

        markManualReviewRequired(state, reason);
    }

    private void markManualReviewRequired(SagaState state, String reason) {
        state.markManualReviewRequired(Instant.now(clock));
        sagaStateRepository.save(state);
        log.warn("Marked saga for manual review. sagaId={}, currentStep={}, sagaStatus={}, reason={}",
                state.getId(), state.getCurrentStep(), state.getSagaStatus(), reason);
    }
}
