package com.haru.money.adapters.in.event.loadmoney;

import com.haru.common.event.OutboxEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoadMoneyFinishedEvent implements OutboxEvent<String, LoadMoneyFinishedEvent> {
    private UUID requestId;
    private String type;
    private Instant timestamp;

    public LoadMoneyFinishedEvent(UUID requestId, String type) {
        this.requestId = requestId;
        this.type = type;
        this.timestamp = Instant.now();
    }

    @Override
    public String aggregateId() {
        return this.requestId.toString();
    }

    @Override
    public String aggregateType() {
        return "LoadMoneyFinishedEvent";
    }

    @Override
    public String type() {
        return this.type;
    }

    @Override
    public Instant timestamp() {
        return this.timestamp;
    }

    @Override
    public LoadMoneyFinishedEvent payload() {
        return this;
    }

    public static LoadMoneyFinishedEvent success(UUID requestId) {
        return new LoadMoneyFinishedEvent(requestId, "success");
    }

    public static LoadMoneyFinishedEvent fail(UUID requestId) {
        return new LoadMoneyFinishedEvent(requestId, "fail");
    }
}
