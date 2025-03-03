package com.haru.orchestrator.adapter.out.persistence.jpa;

import com.haru.orchestrator.adapter.out.persistence.jpa.entity.EventLogJpaEntity;
import com.haru.orchestrator.domain.model.EventLog;
import com.haru.orchestrator.domain.repository.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventLogRepositoryAdapter implements EventLogRepository {
    private final EventLogJpaRepository repository;
    private final EventLogJpaRepository eventLogJpaRepository;

    @Override
    public EventLog findById(UUID eventId) {
        return repository.findById(eventId)
                .map(EventLogConverter::toDomain)
                .orElse(null);
    }

    @Override
    public EventLog save(EventLog eventLog) {
        EventLogJpaEntity entity = eventLogJpaRepository.save(EventLogConverter.toEntity(eventLog));

        return EventLogConverter.toDomain(entity);
    }
}
