package com.haru.orchestrator.adapter.out.persistence.jpa.entity;

import com.haru.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(name = "event_log")
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class EventLogJpaEntity {
    @Id
    @UuidV7Generator
    private UUID eventId;

    private Instant issuedOn;
}
