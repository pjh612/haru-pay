package com.haru.money.adapters.out.persistence.jpa.converter;

import com.haru.money.adapters.out.persistence.jpa.entity.ProcessedEventLogJpaEntity;
import com.haru.money.domain.model.ProcessedEventLog;

public class ProcessedEventLogConverter {
    public static ProcessedEventLogJpaEntity toEntity(ProcessedEventLog domain) {
        return new ProcessedEventLogJpaEntity(
                domain.getId(),
                domain.getConsumerKey(),
                domain.getEventId(),
                domain.getProcessedAt()
        );
    }
}
