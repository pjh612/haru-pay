package com.haru.payments.adapter.out.persistence.jpa;

import com.haru.payments.adapter.out.persistence.jpa.converter.ProcessedEventLogConverter;
import com.haru.payments.domain.model.ProcessedEventLog;
import com.haru.payments.domain.repository.ProcessedEventLogRepository;
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
