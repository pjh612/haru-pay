package com.haru.payments.adapter.out.persistence.jpa;

import com.haru.payments.adapter.out.persistence.jpa.entity.ProcessedEventLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventLogJpaRepository extends JpaRepository<ProcessedEventLogJpaEntity, UUID> {
}
