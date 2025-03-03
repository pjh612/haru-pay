package com.haru.orchestrator.adapter.out.persistence.jpa;

import com.haru.orchestrator.adapter.out.persistence.jpa.entity.EventLogJpaEntity;
import com.haru.orchestrator.domain.model.EventLog;

public class EventLogConverter {

    public static EventLog toDomain(EventLogJpaEntity entity) {
        return new EventLog(entity.getEventId(), entity.getIssuedOn());
    }

    public static EventLogJpaEntity toEntity(EventLog domain) {
        return new EventLogJpaEntity(domain.getEventId(), domain.getIssuedOn());
    }
}
