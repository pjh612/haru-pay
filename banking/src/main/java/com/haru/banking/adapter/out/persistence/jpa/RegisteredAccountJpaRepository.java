package com.haru.banking.adapter.out.persistence.jpa;

import com.haru.banking.adapter.out.persistence.jpa.entity.RegisteredAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegisteredAccountJpaRepository extends JpaRepository<RegisteredAccountJpaEntity, UUID> {
    Optional<RegisteredAccountJpaEntity> findByMemberId(UUID memberId);
}
