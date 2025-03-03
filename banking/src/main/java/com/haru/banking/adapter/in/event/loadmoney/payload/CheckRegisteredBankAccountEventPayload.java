package com.haru.banking.adapter.in.event.loadmoney.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckRegisteredBankAccountEventPayload {
    private UUID loadMoneyRequestId;
    private UUID moneyId;
    private UUID memberId;
    private BigDecimal amount;
    private String type;
}
