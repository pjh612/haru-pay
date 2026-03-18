package com.haru.payments.application.dto;

public record CreateClientRequest(
        String email,
        String name,
        String password
) {
}
