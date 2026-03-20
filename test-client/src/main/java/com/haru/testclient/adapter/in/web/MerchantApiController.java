package com.haru.testclient.adapter.in.web;

import com.haru.testclient.application.dto.OrderSummary;
import com.haru.testclient.application.service.MerchantPaymentService;
import com.haru.testclient.application.service.MerchantRegistrationService;
import com.haru.testclient.application.service.OrderService;
import com.haru.testclient.domain.model.MerchantSession;
import com.haru.testclient.domain.model.PreparedPayment;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/demo/api")
public class MerchantApiController {

    private final MerchantRegistrationService registrationService;
    private final MerchantPaymentService paymentService;
    private final OrderService orderService;

    public MerchantApiController(MerchantRegistrationService registrationService,
                                 MerchantPaymentService paymentService,
                                 OrderService orderService) {
        this.registrationService = registrationService;
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<Map<String, String>> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(Map.of("username", user.getUsername()));
    }

    @GetMapping("/csrf")
    @ResponseBody
    public ResponseEntity<Map<String, String>> csrf(CsrfToken csrfToken) {
        return ResponseEntity.ok(Map.of(
                "token", csrfToken.getToken(),
                "headerName", csrfToken.getHeaderName(),
                "parameterName", csrfToken.getParameterName()
        ));
    }

    @PostMapping("/payments/prepare")
    public ResponseEntity<PreparedPayment> preparePayment(
            @RequestBody com.haru.testclient.application.dto.PreparePaymentRequest request,
            @AuthenticationPrincipal UserDetails user,
            @RequestHeader(value = "Idempotency-Key", required = false) @Size(max = 300) String idempotencyKey) {

        MerchantSession merchant = registrationService.getMerchant();
        PreparedPayment payment = paymentService.preparePayment(
                merchant,
                user.getUsername(),
                request.orderId(),
                request.productName(),
                request.requestPrice(),
                idempotencyKey
        );

        return ResponseEntity.ok(payment);
    }

    @PostMapping("/payments/{paymentId}/confirm")
    public ResponseEntity<PreparedPayment> confirmPayment(@PathVariable UUID paymentId,
                                                          @RequestBody ConfirmWithVerificationRequest request,
                                                          @AuthenticationPrincipal UserDetails user,
                                                          @RequestHeader(value = "Idempotency-Key", required = false) @Size(max = 300) String idempotencyKey) {
        MerchantSession merchant = registrationService.getMerchant();
        PreparedPayment result = paymentService.confirmRequestedPayment(
                merchant,
                paymentId,
                user.getUsername(),
                request.orderId(),
                request.requestPrice(),
                idempotencyKey
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PreparedPayment>> getPayments(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(paymentService.getPayments(user.getUsername()));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderSummary>> getOrders(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(orderService.getOrders(user.getUsername()));
    }

    @DeleteMapping("/session")
    public ResponseEntity<Void> clearSession(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    record ConfirmWithVerificationRequest(String orderId, java.math.BigDecimal requestPrice) {
    }
}
