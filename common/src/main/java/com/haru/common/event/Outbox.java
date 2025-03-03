package com.haru.common.event;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "outbox_event")
@NoArgsConstructor(access = PROTECTED)
class Outbox implements Serializable {

    @Id
    private UUID id;
    private Instant timestamp;

    @Column(name = "aggregateid")
    private String aggregateId;

    @Column(name = "aggregatetype")
    private String aggregateType;
    private String type;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Object payload;

    Outbox(OutboxEvent<?, ?> event, String payload) {
        requireNonNull(event, "event cannot be null");
        this.id = UUID.randomUUID();
        this.timestamp = requireNonNull(event.timestamp(), "issuedOn cannot be null");
        this.aggregateId = requireNonNull(event.aggregateId(), "aggregateId cannot be null").toString();
        this.aggregateType = requireNonNull(event.aggregateType(), "aggregateType cannot be null");
        this.type = requireNonNull(event.type(), "type cannot be null");
        this.payload = requireNonNull(payload, "payload cannot be null");
    }
}
