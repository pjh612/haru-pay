package com.haru.payments.adapter.out.persistence.jpa.entity;

import com.haru.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_processed_event_log", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_processed_event_log_consumer_event", columnNames = {"consumer_key", "event_id"})
})
public class ProcessedEventLogJpaEntity {
    @Id
    @UuidV7Generator
    private UUID id;

    @Column(nullable = false, length = 100)
    private String consumerKey;

    @Column(nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private Instant processedAt;
}
