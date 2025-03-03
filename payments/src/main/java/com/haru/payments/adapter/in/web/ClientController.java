package com.haru.payments.adapter.in.web;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.CreateClientRequest;
import com.haru.payments.application.usecase.CreateClientUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clients")
public class ClientController {
    private final CreateClientUseCase createClientUseCase;

    @PostMapping
    public ClientResponse createClient(@RequestBody CreateClientRequest request) {
        return createClientUseCase.create(request);
    }
}
