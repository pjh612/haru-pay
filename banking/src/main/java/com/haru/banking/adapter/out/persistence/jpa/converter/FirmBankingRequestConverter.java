package com.haru.banking.adapter.out.persistence.jpa.converter;

import com.haru.banking.adapter.out.persistence.jpa.entity.FirmBankingRequestJpaEntity;
import com.haru.banking.domain.model.FirmBankingRequest;

import java.util.Optional;
import java.util.UUID;

public class FirmBankingRequestConverter {
    public static FirmBankingRequest toDomain(FirmBankingRequestJpaEntity entity) {
        return new FirmBankingRequest(entity.getId(),
                entity.getFromBankName(),
                entity.getFromBankAccountNumber(),
                entity.getToBankName(),
                entity.getToBankAccountNumber(),
                entity.getAmount(),
                entity.getStatus()
        );
    }

    public static FirmBankingRequestJpaEntity toEntity(FirmBankingRequest domain) {
        return new FirmBankingRequestJpaEntity(
                domain.getId(),
                domain.getFromBankName(),
                domain.getFromBankAccountNumber(),
                domain.getToBankName(),
                domain.getToBankAccountNumber(),
                domain.getAmount(),
                domain.getStatus()
        );
    }
}
