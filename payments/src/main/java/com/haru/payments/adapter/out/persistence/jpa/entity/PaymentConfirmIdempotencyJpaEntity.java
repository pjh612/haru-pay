package com.haru.payments.adapter.out.persistence.jpa.entity;

import com.haru.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_confirm_idempotency", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_confirm_idempotency_client_key", columnNames = {"client_id", "idempotency_key"})
})
public class PaymentConfirmIdempotencyJpaEntity {
    @Id
    @UuidV7Generator
    private UUID id;

    private UUID clientId;

    @Column(length = 300, nullable = false)
    private String idempotencyKey;

    private UUID paymentId;

    private Instant createdAt;
}
