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
import java.util.UUID;

@Getter
@Entity
@Table(name = "money")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoneyJpaEntity {
    @Id
    @UuidV7Generator
    private UUID id;
    private UUID memberId;
    private BigDecimal balance;
}
