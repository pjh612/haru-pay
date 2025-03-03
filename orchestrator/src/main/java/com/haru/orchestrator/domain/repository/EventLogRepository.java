package com.haru.orchestrator.domain.repository;

import com.haru.orchestrator.domain.model.EventLog;

import java.util.UUID;

public interface EventLogRepository {
    EventLog findById(UUID eventId);

    EventLog save(EventLog eventLog);
}
