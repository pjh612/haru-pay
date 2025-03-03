package com.haru.money.adapters.out.persistence.jpa;

import com.haru.money.adapters.out.persistence.jpa.entity.MoneyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MoneyJpaRepository extends JpaRepository<MoneyJpaEntity, UUID> {
    Optional<MoneyJpaEntity> findByMemberId(UUID memberId);
}
