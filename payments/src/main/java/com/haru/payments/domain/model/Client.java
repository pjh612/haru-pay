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
    private String email;
    private String name;
    private String apiKey;
    private String password;
    private boolean emailVerified;
    private boolean active;
    private Instant createdAt;

    public static Client create(UUID id, String email, String name, String apiKey, String password) {
        return new Client(id, email, name, apiKey, password, false, true, Instant.now());
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public boolean isLoginAllowed() {
        return this.active && this.emailVerified;
    }

    public Client withApiKey(String newApiKey) {
        return new Client(id, email, name, newApiKey, password, emailVerified, active, createdAt);
    }

    public Client withActive(boolean active) {
        return new Client(id, email, name, apiKey, password, emailVerified, active, createdAt);
    }
}
