package com.haru.orchestrator.adapter.out.persistence.jpa;

import com.haru.orchestrator.adapter.out.persistence.jpa.entity.SagaStateJpaEntity;
import com.haru.orchestrator.domain.model.SagaState;

public class SagaStateConverter {
    public static SagaState toDomain(SagaStateJpaEntity entity) {
        return new SagaState(
                entity.getId(),
                entity.getVersion(),
                entity.getType(),
                entity.getPayload(),
                entity.getCurrentStep(),
                entity.getStepStatus(),
                entity.getSagaStatus()
        );
    }

    public static SagaStateJpaEntity toEntity(SagaState domain) {
        return new SagaStateJpaEntity(
                domain.getId(),
                domain.getVersion(),
                domain.getType(),
                domain.getPayload(),
                domain.getCurrentStep(),
                domain.getStepStatus(),
                domain.getSagaStatus()
        );
    }
}
