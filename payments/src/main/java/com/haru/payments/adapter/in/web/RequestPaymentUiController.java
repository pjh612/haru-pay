package com.haru.payments.adapter.in.web;

import com.haru.payments.application.dto.PaymentPageResponse;
import com.haru.payments.application.usecase.QueryPaymentPageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/pay")
@RequiredArgsConstructor
public class RequestPaymentUiController {
    private final QueryPaymentPageUseCase queryPaymentPageUseCase;

    @GetMapping("/{paymentRequestKey}")
    public String payRequest(Model model, @PathVariable String paymentRequestKey, @AuthenticationPrincipal OAuth2User oAuth2User) {
        UUID userId = UUID.fromString(oAuth2User.getAttribute("id"));
        PaymentPageResponse page = queryPaymentPageUseCase.query(UUID.fromString(paymentRequestKey), userId);

        model.addAttribute("shortfallAmount", page.shortfallAmount());
        model.addAttribute("paymentResponse", page);
        model.addAttribute("registeredBankAccountId", page.registeredBankAccountId());
        model.addAttribute("registeredBankName", page.registeredBankName());
        model.addAttribute("registeredBankAccountNumber", page.registeredBankAccountNumber());
        model.addAttribute("clientName", page.clientName());
        model.addAttribute("paymentId", paymentRequestKey);
        model.addAttribute("amount", page.requestPrice());
        model.addAttribute("moneyBalance", page.moneyBalance());

        return "pay";
    }

    @GetMapping("/{paymentRequestKey}/payment-result")
    public String paymentResult(Model model, @PathVariable String paymentRequestKey) {
        model.addAttribute("paymentId", paymentRequestKey);
        return "paymentresult";
    }
}
