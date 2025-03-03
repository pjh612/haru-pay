package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.CreateClientRequest;

import java.util.UUID;

public interface CreateClientUseCase {
    ClientResponse create(CreateClientRequest request);
}
