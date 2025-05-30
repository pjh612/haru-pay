package com.haru.payments.adapter.in.web;

import com.haru.payments.application.client.BankingClient;
import com.haru.payments.application.client.MoneyClient;
import com.haru.payments.application.client.dto.MoneyResponse;
import com.haru.payments.application.client.dto.RegisteredBankAccountResponse;
import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.usecase.QueryClientUseCase;
import com.haru.payments.application.usecase.QueryPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Controller
@RequestMapping("/pay")
@RequiredArgsConstructor
public class RequestPaymentUiController {
    private final MoneyClient moneyClient;
    private final BankingClient bankingClient;
    private final QueryPaymentUseCase queryPaymentUseCase;
    private final QueryClientUseCase queryClientUseCase;

    @GetMapping("/{paymentRequestKey}")
    public String payRequest(Model model, @PathVariable String paymentRequestKey, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String userId = oAuth2User.getAttribute("id");
        PaymentResponse paymentResponse = queryPaymentUseCase.queryById(UUID.fromString(paymentRequestKey));
        MoneyResponse moneyResponse = moneyClient.getMemberById(UUID.fromString(userId));
        ClientResponse clientResponse = queryClientUseCase.queryById(paymentResponse.clientId());
        RegisteredBankAccountResponse registeredBankAccount = bankingClient.getRegisteredBankAccount(UUID.fromString(userId));

        BigDecimal shortage = paymentResponse.requestPrice().subtract(moneyResponse.balance());
        BigDecimal shortfallAmount = shortage.divide(BigDecimal.TEN.pow(4), RoundingMode.UP);
        shortfallAmount = shortfallAmount.setScale(0, RoundingMode.UP).multiply(BigDecimal.TEN.pow(4));

        model.addAttribute("shortfallAmount", shortfallAmount);
        model.addAttribute("paymentResponse", paymentResponse);
        model.addAttribute("registeredBankAccountId", registeredBankAccount.id());
        model.addAttribute("registeredBankName", registeredBankAccount.bankName());
        model.addAttribute("registeredBankAccountNumber", registeredBankAccount.accountNumber());
        model.addAttribute("clientName", clientResponse.name());
        model.addAttribute("paymentId", paymentRequestKey);
        model.addAttribute("amount", paymentResponse.requestPrice());
        model.addAttribute("moneyBalance", moneyResponse.balance());

        return "pay";
    }

    @GetMapping("/{paymentRequestKey}/payment-result")
    public String paymentResult(Model model, @PathVariable String paymentRequestKey) {
        model.addAttribute("paymentId", paymentRequestKey);
        return "paymentresult";
    }
}
