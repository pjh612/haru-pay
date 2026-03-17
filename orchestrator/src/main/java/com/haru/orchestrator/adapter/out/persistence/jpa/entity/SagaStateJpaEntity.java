package com.haru.orchestrator.adapter.out.persistence.jpa.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import com.haru.orchestrator.domain.model.SagaStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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

    private String currentStep;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private ObjectNode stepStatus;

    @Enumerated(EnumType.STRING)
    private SagaStatus sagaStatus;
}
