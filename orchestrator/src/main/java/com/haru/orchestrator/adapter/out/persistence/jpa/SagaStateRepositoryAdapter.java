package com.haru.orchestrator.adapter.out.persistence.jpa;

import com.haru.orchestrator.adapter.out.persistence.jpa.entity.SagaStateJpaEntity;
import com.haru.orchestrator.domain.model.SagaState;
import com.haru.orchestrator.domain.repository.SagaStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SagaStateRepositoryAdapter implements SagaStateRepository {
    private final SagaStateJpaRepository repository;
    private final SagaStateJpaRepository sagaStateJpaRepository;

    @Override
    public SagaState save(SagaState sagaState) {
        SagaStateJpaEntity entity = repository.save(SagaStateConverter.toEntity(sagaState));

        return SagaStateConverter.toDomain(entity);
    }

    @Override
    public Optional<SagaState> findById(UUID sagaId) {
        return sagaStateJpaRepository.findById(sagaId)
                .map(SagaStateConverter::toDomain);
    }
}
