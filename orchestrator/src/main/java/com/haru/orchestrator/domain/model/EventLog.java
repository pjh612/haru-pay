package com.haru.orchestrator.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventLog {
    private UUID eventId;

    private Instant issuedOn;

    public static EventLog createNew(UUID eventId) {
        return new EventLog(eventId, Instant.now());
    }
}
