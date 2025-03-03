package com.haru.money.adapters.out.persistence.jpa.converter;

import com.haru.money.adapters.out.persistence.jpa.entity.MoneyJpaEntity;
import com.haru.money.domain.model.Money;

public class MoneyConverter {
    public static Money toDomain(MoneyJpaEntity entity) {
        return new Money(
                entity.getId(),
                entity.getMemberId(),
                entity.getBalance()
        );
    }

    public static MoneyJpaEntity toEntity(Money money) {
        return new MoneyJpaEntity(
                money.getId(),
                money.getMemberId(),
                money.getBalance()
        );
    }
}
