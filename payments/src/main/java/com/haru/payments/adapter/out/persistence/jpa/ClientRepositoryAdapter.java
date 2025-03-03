package com.haru.payments.adapter.out.persistence.jpa;

import com.haru.payments.adapter.out.persistence.jpa.converter.ClientConverter;
import com.haru.payments.adapter.out.persistence.jpa.entity.ClientJpaEntity;
import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepository {
    private final ClientJpaRepository repository;

    @Override
    public Client save(Client client) {
        ClientJpaEntity entity = ClientConverter.toEntity(client);

        return ClientConverter.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return repository.findById(id)
                .map(ClientConverter::toDomain);
    }
}
