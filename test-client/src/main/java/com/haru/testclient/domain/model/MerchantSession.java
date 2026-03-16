package com.haru.testclient.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSession {
    private UUID clientId;
    private String clientName;
    private String apiKey;
    private Instant registeredAt;
}
