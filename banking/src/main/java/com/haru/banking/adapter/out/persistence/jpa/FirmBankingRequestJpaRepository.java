package com.haru.banking.adapter.out.persistence.jpa;

import com.haru.banking.adapter.out.persistence.jpa.entity.FirmBankingRequestJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FirmBankingRequestJpaRepository extends JpaRepository<FirmBankingRequestJpaEntity, UUID> {
}
