package com.haru.payments.application.dto;

public record ClientLoginRequest(
        String email,
        String password
) {
}
