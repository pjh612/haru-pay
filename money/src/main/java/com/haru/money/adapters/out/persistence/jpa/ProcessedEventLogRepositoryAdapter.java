package com.haru.money.adapters.out.persistence.jpa;

import com.haru.money.adapters.out.persistence.jpa.converter.ProcessedEventLogConverter;
import com.haru.money.domain.model.ProcessedEventLog;
import com.haru.money.domain.repository.ProcessedEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProcessedEventLogRepositoryAdapter implements ProcessedEventLogRepository {
    private final ProcessedEventLogJpaRepository repository;

    @Override
    public boolean markIfFirst(String consumerKey, UUID eventId) {
        try {
            repository.saveAndFlush(ProcessedEventLogConverter.toEntity(ProcessedEventLog.createNew(consumerKey, eventId)));
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}
