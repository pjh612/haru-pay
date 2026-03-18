package com.haru.payments.application.port.out.cache;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository {
    void save(String token, UUID clientId);

    Optional<UUID> findClientIdByToken(String token);

    void delete(String token);
}
