package com.haru.money.domain.repository;

import java.util.UUID;

public interface ProcessedEventLogRepository {
    boolean markIfFirst(String consumerKey, UUID eventId);
}
