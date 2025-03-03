package com.haru.money.application.dto;

import java.util.UUID;

public record CreateMoneyRequest(
        UUID memberId
) {
}
