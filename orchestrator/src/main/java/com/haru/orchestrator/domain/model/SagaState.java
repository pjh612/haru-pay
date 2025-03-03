package com.haru.orchestrator.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.UUID;

import static com.haru.orchestrator.domain.model.SagaStepStatus.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SagaState {
    private UUID id;
    private int version;
    private String type;
    private JsonNode payload;
    private String currentStep;
    private ObjectNode stepStatus;
    private SagaStatus sagaStatus;

    public SagaState(UUID sagaId, String sagaType, JsonNode payload) {
        this.id = sagaId;
        this.type = sagaType;
        this.payload = payload;
        this.sagaStatus = SagaStatus.STARTED;
        this.stepStatus = JsonNodeFactory.instance.objectNode();
    }

    public void updateCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public void updateStepStatus(String step, SagaStepStatus sagaStepStatus) {
        this.stepStatus.put(step, sagaStepStatus.name());
    }

    /**
     * Following SagaSteps To SagaStatus mapping:
     * 1. SUCCEEDED -> COMPLETED
     * 2. STARTED, SUCCEEDED -> STARTED
     * 3. FAILED, COMPENSATED -> ABORTED
     * 4. COMPENSATING, other -> ABORTING
     */
    public void advanceSagaStatus() {
        var bitmask = stepStatusToSet().stream()
                .mapToInt(status -> 1 << status.ordinal())
                .reduce(0, (a, b) -> a | b);

        if ((bitmask & (1 << SUCCEEDED.ordinal())) == bitmask) {
            sagaStatus = SagaStatus.COMPLETED;
        } else if ((bitmask & ((1 << STARTED.ordinal()) | (1 << SUCCEEDED.ordinal()))) == bitmask) {
            sagaStatus = SagaStatus.STARTED;
        } else if ((bitmask & ((1 << FAILED.ordinal()) | (1 << COMPENSATED.ordinal()))) == bitmask) {
            sagaStatus = SagaStatus.ABORTED;
        } else {
            sagaStatus = SagaStatus.ABORTING;
        }
    }

    private EnumSet<SagaStepStatus> stepStatusToSet() {
        EnumSet<SagaStepStatus> allStatus = EnumSet.noneOf(SagaStepStatus.class);
        stepStatus.fields()
                .forEachRemaining(entry -> allStatus.add(SagaStepStatus.valueOf(entry.getValue().asText())));

        return allStatus;
    }
}
