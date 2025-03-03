package com.haru.orchestrator.config;

import com.haru.orchestrator.application.SagaStepFlowRegistry;
import com.haru.orchestrator.domain.model.SagaStepFlow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaConfig {

    @Bean
    public SagaStepFlowRegistry sagaStepFlowRegistry() {
        SagaStepFlowRegistry registry = new SagaStepFlowRegistry();

        registry.register("LOAD_MONEY_SAGA", SagaStepFlow.builder()
                .addStep("load-money-request")
                .addStep("validate-bank-account")
                .addStep("request-firm-banking")
                .addStep("load-money")
                .build());

        registry.register("PAYMENT_SAGA", SagaStepFlow.builder()
                .addStep("payment-request")
                .addStep("decrease-money")
                .addStep("confirm-payment")
                .build());

        return registry;
    }
}
