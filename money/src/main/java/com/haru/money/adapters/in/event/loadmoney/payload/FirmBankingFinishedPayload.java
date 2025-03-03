package com.haru.money.adapters.in.event.loadmoney.payload;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FirmBankingFinishedPayload {
    private UUID loadMoneyRequestId;
    private UUID memberId;
    private BigDecimal amount;
    private String type;
}
