package com.haru.payments.adapter.in.web;

import com.haru.payments.application.dto.*;
import com.haru.payments.application.usecase.RequestPaymentUseCase;
import com.haru.payments.application.usecase.SubscribePaymentResultUseCase;
import com.haru.payments.domain.model.Client;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
public class RequestPaymentController {
    private final RequestPaymentUseCase requestPaymentUseCase;
    private final SubscribePaymentResultUseCase subscribePaymentResultUseCase;

    @ResponseBody
    @PostMapping("/api/payment/prepare")
    public PreparePaymentResponse preparePaymentRequest(@Valid @RequestBody PreparePaymentRequest request,
                                                        @RequestHeader(value = "Idempotency-Key", required = false) @Size(max = 300) String idempotencyKey,
                                                        @AuthenticationPrincipal Client client) {
        PreparePaymentCommand command = new PreparePaymentCommand(client.getId(), request.orderId(), request.requestPrice(), request.productName(), idempotencyKey);
        return new PreparePaymentResponse(requestPaymentUseCase.preparePayment(command).requestId());
    }

    @ResponseBody
    @PostMapping("/api/payment/request")
    public RequestPaymentResponse requestPayment(@Valid @RequestBody RequestPaymentRequest request, @AuthenticationPrincipal OAuth2User oAuth2User) {
        UUID userId = UUID.fromString(oAuth2User.getAttribute("id"));
        RequestPaymentCommand command = new RequestPaymentCommand(request.paymentRequestId(), userId);

        return requestPaymentUseCase.requestPayment(command);
    }

    @ResponseBody
    @PostMapping("/api/payment/confirm")
    public void confirmPayment(@Valid @RequestBody ConfirmPaymentRequest request,
                               @RequestHeader(value = "Idempotency-Key", required = false) @Size(max = 300) String idempotencyKey,
                               @AuthenticationPrincipal Client client) {
        PaymentCommand command = new PaymentCommand(request.paymentId(), client.getId(), idempotencyKey);

        requestPaymentUseCase.confirmPayment(command);
    }

    @ResponseBody
    @GetMapping(value = "/api/payment-result/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeAlarm(@RequestParam UUID paymentId,
                                     @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                     @AuthenticationPrincipal Client client) {
        return subscribePaymentResultUseCase.subscribe(paymentId, client.getId(), lastEventId);
    }
}
