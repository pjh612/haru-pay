package com.haru.payments.application.client.dto;

public record MemberResponse(String id,
                             String username,
                             String password,
                             String name,
                             String gender) {
}
