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
    private String password;
    private boolean active;
    private Instant createdAt;

    public static Client create(UUID id, String name, String apiKey) {
        return new Client(id, name, apiKey, null, true, Instant.now());
    }

    public static Client createWithPassword(UUID id, String name, String apiKey, String password) {
        return new Client(id, name, apiKey, password, true, Instant.now());
    }

    public boolean verifyPassword(String rawPassword, org.springframework.security.crypto.password.PasswordEncoder encoder) {
        return this.password != null && encoder.matches(rawPassword, this.password);
    }

    public Client withApiKey(String newApiKey) {
        return new Client(id, name, newApiKey, password, active, createdAt);
    }

    public Client withActive(boolean active) {
        return new Client(id, name, apiKey, password, active, createdAt);
    }
}
