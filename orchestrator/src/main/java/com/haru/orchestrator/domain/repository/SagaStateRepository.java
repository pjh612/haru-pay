package com.haru.orchestrator.domain.repository;

import com.haru.orchestrator.domain.model.SagaState;
import com.haru.orchestrator.domain.model.SagaStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SagaStateRepository {
    SagaState save(SagaState sagaState);

    Optional<SagaState> findById(UUID sagaId);

    List<SagaState> findBySagaStatusAndCurrentStepAndLastProgressAtBefore(SagaStatus sagaStatus, String currentStep, Instant cutoff);
}
