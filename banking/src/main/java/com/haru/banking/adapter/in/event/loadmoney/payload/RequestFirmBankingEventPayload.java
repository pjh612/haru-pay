package com.haru.banking.adapter.in.event.loadmoney.payload;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestFirmBankingEventPayload {
    private UUID loadMoneyRequestId;
    private UUID memberId;
    private String fromBankName;
    private String fromBankAccountNumber;
    private String toBankName;
    private String toBankAccountNumber;
    private BigDecimal amount;
    private String type;
}
