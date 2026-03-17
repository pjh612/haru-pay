package com.haru.testclient.adapter.in.web;

import com.haru.testclient.application.service.MerchantPaymentService;
import com.haru.testclient.application.service.MerchantRegistrationService;
import com.haru.testclient.domain.model.MerchantSession;
import com.haru.testclient.domain.model.PreparedPayment;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/demo/api")
public class MerchantApiController {

    private final MerchantRegistrationService registrationService;
    private final MerchantPaymentService paymentService;
    private final String paymentsCheckoutUrl;

    public MerchantApiController(MerchantRegistrationService registrationService,
                                 MerchantPaymentService paymentService,
                                 @Value("${payments.checkout-url}") String paymentsCheckoutUrl) {
        this.registrationService = registrationService;
        this.paymentService = paymentService;
        this.paymentsCheckoutUrl = paymentsCheckoutUrl;
    }

    @GetMapping("/config")
    public Map<String, String> getConfig() {
        return Map.of("paymentsCheckoutUrl", paymentsCheckoutUrl);
    }

    @GetMapping("/merchant")
    public ResponseEntity<MerchantSession> getCurrentMerchant(HttpSession session) {
        String clientId = (String) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(registrationService.getSession(clientId));
    }

    @PostMapping("/register")
    public ResponseEntity<MerchantSession> register(@RequestParam String name, HttpSession session) {
        MerchantSession merchant = registrationService.registerMerchant(name);
        session.setAttribute("clientId", merchant.getClientId().toString());
        return ResponseEntity.ok(merchant);
    }

    @PostMapping("/payments/prepare")
    public ResponseEntity<PreparedPayment> preparePayment(
            @RequestParam String orderId,
            @RequestParam String productName,
            @RequestParam BigDecimal requestPrice,
            HttpSession session) {
        
        String clientId = (String) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        MerchantSession merchant = registrationService.getSession(clientId);
        PreparedPayment payment = paymentService.preparePayment(merchant, orderId, productName, requestPrice);
        
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/payments/{paymentId}/confirm")
    public ResponseEntity<Void> confirmPayment(@PathVariable UUID paymentId, HttpSession session) {
        String clientId = (String) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        MerchantSession merchant = registrationService.getSession(clientId);
        paymentService.confirmPayment(merchant, paymentId);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PreparedPayment>> getPayments(HttpSession session) {
        String clientId = (String) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(paymentService.getPayments(clientId));
    }

    @DeleteMapping("/session")
    public ResponseEntity<Void> clearSession(HttpSession session) {
        String clientId = (String) session.getAttribute("clientId");
        if (clientId != null) {
            registrationService.clearSession(clientId);
        }
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
