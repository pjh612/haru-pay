package com.haru.orchestrator.adapter.out.persistence.jpa.entity;

import com.haru.orchestrator.domain.model.RecoveryStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import com.haru.orchestrator.domain.model.SagaStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "sagastate")
@NoArgsConstructor(access = PROTECTED)
public class SagaStateJpaEntity {

    @Id
    private UUID id;

    @Version
    private int version;

    private String type;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode payload;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode currentPayload;

    private String currentStep;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private ObjectNode stepStatus;

    @Enumerated(EnumType.STRING)
    private SagaStatus sagaStatus;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastProgressAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecoveryStatus recoveryStatus;

    @Column(nullable = false)
    private int recoveryAttemptCount;

    private Instant lastRecoveryAt;
}
