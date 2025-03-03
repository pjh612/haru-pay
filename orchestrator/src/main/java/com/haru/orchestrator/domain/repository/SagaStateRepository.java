package com.haru.orchestrator.domain.repository;

import com.haru.orchestrator.domain.model.SagaState;

import java.util.Optional;
import java.util.UUID;

public interface SagaStateRepository {
    SagaState save(SagaState sagaState);

    Optional<SagaState> findById(UUID sagaId);
}
