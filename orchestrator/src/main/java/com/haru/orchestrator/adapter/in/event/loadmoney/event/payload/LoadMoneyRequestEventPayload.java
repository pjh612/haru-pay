package com.haru.orchestrator.adapter.in.event.loadmoney.event.payload;

import com.haru.orchestrator.domain.model.PayloadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadMoneyRequestEventPayload {
    private String requestId;
    private String memberId;
    private BigDecimal amount;
    private PayloadType type;
}
