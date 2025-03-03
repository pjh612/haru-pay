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
public class Client {
    private UUID id;
    private String name;
    private String apiKey;
    private boolean active;
    private Instant createdAt;

    public static Client create(UUID id, String name, String apiKey) {
        return new Client(id, name, apiKey, true, Instant.now());
    }
}
