package com.haru.payments.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEventLog {
    private UUID id;
    private String consumerKey;
    private UUID eventId;
    private Instant processedAt;

    public static ProcessedEventLog createNew(String consumerKey, UUID eventId) {
        return new ProcessedEventLog(null, consumerKey, eventId, Instant.now());
    }
}
