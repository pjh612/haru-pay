package com.haru.orchestrator.adapter.out.event;

import com.haru.orchestrator.application.SagaEventPublisher;
import com.haru.orchestrator.domain.model.SagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringSagaEventPublisher implements SagaEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishEvent(SagaEvent sagaEvent) {
        eventPublisher.publishEvent(sagaEvent);
    }
}
