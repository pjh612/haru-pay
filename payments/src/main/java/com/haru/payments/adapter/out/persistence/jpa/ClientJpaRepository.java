package com.haru.payments.adapter.out.persistence.jpa;

import com.haru.payments.adapter.out.persistence.jpa.entity.ClientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, UUID> {
}
