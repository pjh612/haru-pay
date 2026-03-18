package com.haru.orchestrator.adapter.out.persistence.jpa;

import com.haru.orchestrator.adapter.out.persistence.jpa.entity.SagaStateJpaEntity;
import com.haru.orchestrator.domain.model.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SagaStateJpaRepository extends JpaRepository<SagaStateJpaEntity, UUID> {
    List<SagaStateJpaEntity> findBySagaStatusAndCurrentStepAndLastProgressAtBefore(SagaStatus sagaStatus, String currentStep, Instant cutoff);
}
