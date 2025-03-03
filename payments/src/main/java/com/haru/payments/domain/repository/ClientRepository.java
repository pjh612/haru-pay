package com.haru.payments.domain.repository;

import com.haru.payments.domain.model.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);

    Optional<Client> findById(UUID id);
}
