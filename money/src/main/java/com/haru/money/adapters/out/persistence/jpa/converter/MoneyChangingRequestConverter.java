package com.haru.money.adapters.out.persistence.jpa.converter;

import com.haru.money.adapters.out.persistence.jpa.entity.MoneyChangingRequestJpaEntity;
import com.haru.money.domain.model.ChangingStatus;
import com.haru.money.domain.model.ChangingType;
import com.haru.money.domain.model.MoneyChangingRequest;

public class MoneyChangingRequestConverter {
    public static MoneyChangingRequestJpaEntity toEntity(MoneyChangingRequest moneyChangingRequest) {
        return new MoneyChangingRequestJpaEntity(
                moneyChangingRequest.getId(),
                moneyChangingRequest.getTargetMemberId(),
                moneyChangingRequest.getChangingType().name(),
                moneyChangingRequest.getAmount(),
                moneyChangingRequest.getStatus().name(),
                moneyChangingRequest.getCreatedAt()
        );
    }

    public static MoneyChangingRequest toDomain(MoneyChangingRequestJpaEntity entity) {
        return new MoneyChangingRequest(
                entity.getId(),
                entity.getTargetMemberId(),
                ChangingType.valueOf(entity.getChangingType()),
                entity.getAmount(),
                ChangingStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}
