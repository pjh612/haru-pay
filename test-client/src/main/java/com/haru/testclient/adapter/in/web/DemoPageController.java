package com.haru.testclient.adapter.in.web;

import com.haru.testclient.application.service.MerchantPaymentService;
import com.haru.testclient.application.service.MerchantRegistrationService;
import com.haru.testclient.domain.model.MerchantSession;
import com.haru.testclient.domain.model.PreparedPayment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Controller
public class DemoPageController {

    private final AuthenticationManager authenticationManager;
    private final MerchantRegistrationService registrationService;
    private final MerchantPaymentService paymentService;

    public DemoPageController(AuthenticationManager authenticationManager,
                              MerchantRegistrationService registrationService,
                              MerchantPaymentService paymentService) {
        this.authenticationManager = authenticationManager;
        this.registrationService = registrationService;
        this.paymentService = paymentService;
    }


    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @PostMapping("/demo/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String username = body.get("username");
        String password = body.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession(true)
                    .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                            SecurityContextHolder.getContext());

            return ResponseEntity.ok(Map.of("username", authentication.getName()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    @GetMapping("/demo/api/me")
    @ResponseBody
    public ResponseEntity<Map<String, String>> me(@AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(Map.of("username", user.getUsername()));
    }

    @GetMapping(value = "/demo/payments/success", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ResponseEntity<String> success(@RequestParam(required = false) UUID requestId,
                                          @RequestParam(required = false) UUID paymentId,
                                          @RequestParam String orderId,
                                          @RequestParam BigDecimal requestPrice) {
        UUID resolvedPaymentId = paymentId != null ? paymentId : requestId;
        if (resolvedPaymentId == null) {
            return relayFailure("INVALID_PAYMENT", "결제 식별자가 없습니다.", orderId, null);
        }

        MerchantSession merchant = registrationService.getMerchant();

        try {
            PreparedPayment payment = paymentService.confirmPreparedPayment(merchant, resolvedPaymentId, orderId, requestPrice);
            return relaySuccess(payment);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return relayFailure("PAYMENT_CONFIRM_REJECTED", e.getMessage(), orderId, resolvedPaymentId);
        } catch (RuntimeException e) {
            return relayFailure("PAYMENT_CONFIRM_FAILED", "결제 확정 요청 처리에 실패했습니다.", orderId, resolvedPaymentId);
        }
    }

    @GetMapping(value = "/demo/payments/failure", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ResponseEntity<String> failure(@RequestParam(required = false) String errorCode,
                                          @RequestParam(required = false) String message,
                                          @RequestParam(required = false) String orderId,
                                          @RequestParam(required = false) UUID paymentId) {
        return relayFailure(
                errorCode != null ? errorCode : "PAYMENT_FAILED",
                message != null ? message : "결제창 처리에 실패했습니다.",
                orderId,
                paymentId
        );
    }

    private ResponseEntity<String> relaySuccess(PreparedPayment payment) {
        return htmlResponse(
                "HaruPay Success",
                "결제 확정 요청을 처리했습니다. 창을 닫는 중입니다...",
                Map.of(
                        "source", "harupay-success",
                        "requestId", payment.getPaymentId().toString(),
                        "paymentId", payment.getPaymentId().toString(),
                        "orderId", payment.getOrderId(),
                        "requestPrice", payment.getRequestPrice(),
                        "paymentStatus", 0,
                        "status", payment.getStatus()
                )
        );
    }

    private ResponseEntity<String> relayFailure(String errorCode, String message, String orderId, UUID paymentId) {
        return htmlResponse(
                "HaruPay Failure",
                "결제창 처리 중 오류가 발생했습니다.",
                Map.of(
                        "source", "harupay-failure",
                        "errorCode", errorCode,
                        "message", message,
                        "orderId", orderId != null ? orderId : "",
                        "paymentId", paymentId != null ? paymentId.toString() : ""
                )
        );
    }

    private ResponseEntity<String> htmlResponse(String title, String message, Map<String, Object> payload) {
        String html = "<!doctype html><html lang=\"ko\"><head><meta charset=\"UTF-8\" />"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />"
                + "<title>" + escapeHtml(title) + "</title></head><body><p>" + escapeHtml(message) + "</p><script>"
                + "const payload=" + toJson(payload) + ";"
                + "if (window.opener) { window.opener.postMessage(payload, window.location.origin); }"
                + "window.setTimeout(() => window.close(), 300);"
                + "</script></body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    private String toJson(Map<String, Object> payload) {
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append('"').append(escapeJson(entry.getKey())).append('"').append(':');

            Object value = entry.getValue();
            if (value == null) {
                builder.append("null");
            } else if (value instanceof Number || value instanceof Boolean) {
                builder.append(value);
            } else {
                builder.append('"').append(escapeJson(String.valueOf(value))).append('"');
            }
        }
        builder.append('}');
        return builder.toString();
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
