package com.haru.payments.adapter.out.persistence.jpa.entity;

import com.haru.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "payment_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRequestJpaEntity {
    @Id
    @UuidV7Generator
    private UUID requestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private UUID clientId;
    private int paymentStatus;
    private Instant approvedAt;
    private Instant createdAt;
}
