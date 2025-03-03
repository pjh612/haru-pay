package com.haru.orchestrator.adapter.in.event.loadmoney.event.payload;

import com.haru.orchestrator.application.SagaPayload;
import com.haru.orchestrator.domain.model.PayloadType;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoadMoneyFinishedEventPayload implements SagaPayload {
    private String status;
    private String loadMoneyRequestId;
    private PayloadType type;

    @Override
    public PayloadType type() {
        return this.type;
    }

    @Override
    public Object toEvent() {
        return null;
    }
}
