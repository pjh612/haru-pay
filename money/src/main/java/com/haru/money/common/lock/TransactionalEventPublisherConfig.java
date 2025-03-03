package com.haru.money.common.lock;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.reactive.TransactionalEventPublisher;

@Controller
public class TransactionalEventPublisherConfig {
    @Bean
    public TransactionalEventPublisher transactionalEventPublisher(ApplicationEventPublisher eventPublisher) {
        return new TransactionalEventPublisher(eventPublisher);
    }
}
