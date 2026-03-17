package com.haru.testclient.application.service;

import com.haru.testclient.adapter.out.client.PaymentsSseClient;
import com.haru.testclient.domain.model.MerchantSession;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PaymentResultRelayService {

    private final PaymentsSseClient sseClient;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public PaymentResultRelayService(PaymentsSseClient sseClient) {
        this.sseClient = sseClient;
    }

    public SseEmitter createEmitter(MerchantSession session, UUID paymentId) {
        SseEmitter emitter = new SseEmitter(600_000L); // 10 minutes timeout
        
        executorService.execute(() -> {
            try {
                Flux<String> upstreamFlux = sseClient.subscribeToPaymentResults(
                        session.getClientId().toString(),
                        session.getApiKey(),
                        paymentId
                );
                
                upstreamFlux.subscribe(
                        event -> {
                            try {
                                emitter.send(SseEmitter.event()
                                        .data(event));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        error -> emitter.completeWithError(error),
                        () -> emitter.complete()
                );
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        emitter.onCompletion(() -> System.out.println("SSE completed for payment: " + paymentId));
        emitter.onTimeout(() -> System.out.println("SSE timeout for payment: " + paymentId));
        
        return emitter;
    }
}
