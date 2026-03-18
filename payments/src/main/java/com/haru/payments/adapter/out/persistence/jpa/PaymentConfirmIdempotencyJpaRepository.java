package com.haru.payments.adapter.out.persistence.jpa;

import com.haru.payments.adapter.out.persistence.jpa.entity.PaymentConfirmIdempotencyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentConfirmIdempotencyJpaRepository extends JpaRepository<PaymentConfirmIdempotencyJpaEntity, UUID> {
    Optional<PaymentConfirmIdempotencyJpaEntity> findByClientIdAndIdempotencyKey(UUID clientId, String idempotencyKey);
}
