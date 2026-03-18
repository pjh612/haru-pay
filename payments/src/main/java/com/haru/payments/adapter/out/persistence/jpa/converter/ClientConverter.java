package com.haru.payments.adapter.out.persistence.jpa.converter;

import com.haru.payments.adapter.out.persistence.jpa.entity.ClientJpaEntity;
import com.haru.payments.domain.model.Client;

public class ClientConverter {
    public static Client toDomain(ClientJpaEntity entity) {
        return new Client(
                entity.getId(),
                entity.getEmail(),
                entity.getName(),
                entity.getApiKey(),
                entity.getPassword(),
                entity.isEmailVerified(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }

    public static ClientJpaEntity toEntity(Client domain) {
        return new ClientJpaEntity(
                domain.getId(),
                domain.getEmail(),
                domain.getName(),
                domain.getApiKey(),
                domain.getPassword(),
                domain.isEmailVerified(),
                domain.isActive(),
                domain.getCreatedAt()
        );
    }
}
