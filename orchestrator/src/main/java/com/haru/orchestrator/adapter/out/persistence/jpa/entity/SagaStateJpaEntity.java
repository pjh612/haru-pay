package com.haru.orchestrator.adapter.out.persistence.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haru.orchestrator.domain.model.SagaStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode payload;

    private String currentStep;

    @JdbcTypeCode(SqlTypes.JSON)
    private ObjectNode stepStatus;

    @Enumerated(EnumType.STRING)
    private SagaStatus sagaStatus;
}
