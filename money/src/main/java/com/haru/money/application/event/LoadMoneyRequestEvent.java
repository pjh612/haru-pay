package com.haru.money.application.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.haru.common.event.OutboxEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoadMoneyRequestEvent implements OutboxEvent<String, LoadMoneyRequestEvent> {
    private UUID requestId;
    private String memberId;
    private BigDecimal amount;
    private Instant timestamp;

    public LoadMoneyRequestEvent(UUID requestId, String memberId, BigDecimal amount) {
        this.requestId = requestId;
        this.memberId = memberId;
        this.amount = amount;
        this.timestamp = Instant.now();
    }

    @Override
    public String aggregateId() {
        return this.requestId.toString();
    }

    @Override
    public String aggregateType() {
        return "LoadMoneyRequestEvent";
    }

    @Override
    public String type() {
        return "REQUEST";
    }

    @Override
    public Instant timestamp() {
        return this.timestamp;
    }

    @Override
    public LoadMoneyRequestEvent payload() {
        return this;
    }
}
