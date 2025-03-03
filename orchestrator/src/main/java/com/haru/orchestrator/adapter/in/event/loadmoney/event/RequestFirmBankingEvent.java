package com.haru.orchestrator.adapter.in.event.loadmoney.event;

import lombok.*;

import java.math.BigDecimal;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestFirmBankingEvent {
    private String loadMoneyRequestId;
    private String memberId;
    private String fromBankName;
    private String fromBankAccountNumber;
    private String toBankName;
    private String toBankAccountNumber;
    private BigDecimal amount;
}
