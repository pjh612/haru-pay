package com.haru.payments.application.usecase;


import com.haru.payments.application.dto.ClientResponse;

import java.util.UUID;

public interface QueryClientUseCase {
    ClientResponse queryById(UUID id);
}
