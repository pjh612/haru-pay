package com.haru.payments.adapter.out.persistence.jpa.entity;

import com.haru.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "client")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientJpaEntity {
    @Id
    @UuidV7Generator
    private UUID id;
    private String name;
    private String apiKey;
    private boolean active;
    private Instant createdAt;
}
