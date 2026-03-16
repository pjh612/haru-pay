package com.haru.testclient.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreparedPayment {
    private UUID paymentId;
    private String orderId;
    private String productName;
    private BigDecimal requestPrice;
    private Instant preparedAt;
    private String status;
}
