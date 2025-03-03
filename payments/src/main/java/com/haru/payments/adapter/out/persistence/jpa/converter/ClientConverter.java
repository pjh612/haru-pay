package com.haru.payments.adapter.out.persistence.jpa.converter;

import com.haru.payments.adapter.out.persistence.jpa.entity.ClientJpaEntity;
import com.haru.payments.domain.model.Client;

public class ClientConverter {
    public static Client toDomain(ClientJpaEntity entity) {
        return new Client(
                entity.getId(),
                entity.getName(),
                entity.getApiKey(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }

    public static ClientJpaEntity toEntity(Client domain) {
        return new ClientJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getApiKey(),
                domain.isActive(),
                domain.getCreatedAt()
        );
    }
}
