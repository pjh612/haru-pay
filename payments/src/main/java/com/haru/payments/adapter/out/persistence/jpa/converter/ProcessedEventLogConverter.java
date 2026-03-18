package com.haru.payments.adapter.out.persistence.jpa.converter;

import com.haru.payments.adapter.out.persistence.jpa.entity.ProcessedEventLogJpaEntity;
import com.haru.payments.domain.model.ProcessedEventLog;

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
