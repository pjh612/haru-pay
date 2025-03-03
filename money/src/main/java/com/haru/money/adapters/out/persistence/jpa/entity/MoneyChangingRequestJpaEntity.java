package com.haru.money.adapters.out.persistence.jpa.entity;

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

@Getter
@Entity
@AllArgsConstructor
@Table(name = "money_changing_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoneyChangingRequestJpaEntity {
    @Id
    @UuidV7Generator
    private UUID id;
    private UUID targetMemberId;
    private String changingType;
    private BigDecimal amount;
    private String status;
    private Instant createdAt;
}
