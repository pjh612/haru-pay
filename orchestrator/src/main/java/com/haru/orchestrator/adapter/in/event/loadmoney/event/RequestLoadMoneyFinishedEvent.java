package com.haru.orchestrator.adapter.in.event.loadmoney.event;

import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestLoadMoneyFinishedEvent {
    private String loadMoneyRequestId;
    private String memberId;
    private BigDecimal amount;
}
