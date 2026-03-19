package com.haru.testclient.adapter.in.web;

import com.haru.testclient.application.service.MerchantRegistrationService;
import com.haru.testclient.application.service.PaymentResultRelayService;
import com.haru.testclient.domain.model.MerchantSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/demo/stream")
public class PaymentStreamController {

    private final PaymentResultRelayService relayService;
    private final MerchantRegistrationService registrationService;

    public PaymentStreamController(PaymentResultRelayService relayService,
                                   MerchantRegistrationService registrationService) {
        this.relayService = relayService;
        this.registrationService = registrationService;
    }

    @GetMapping("/payments/{paymentId}")
    public SseEmitter streamPaymentResults(@PathVariable UUID paymentId) {
        MerchantSession merchant = registrationService.getMerchant();
        return relayService.createEmitter(merchant, paymentId);
    }
}
