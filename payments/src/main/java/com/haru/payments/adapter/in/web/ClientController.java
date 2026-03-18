package com.haru.payments.adapter.in.web;

import com.haru.payments.adapter.in.security.ClientEmailPasswordAuthenticationToken;
import com.haru.payments.application.dto.*;
import com.haru.payments.application.usecase.CreateClientUseCase;
import com.haru.payments.application.usecase.VerifyEmailUseCase;
import com.haru.payments.domain.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clients")
public class ClientController {
    private final CreateClientUseCase createClientUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ClientResponse createClient(@RequestBody CreateClientRequest request) {
        return createClientUseCase.create(request);
    }

    @PostMapping("/login")
    public ResponseEntity<ClientLoginResponse> login(@RequestBody ClientLoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new ClientEmailPasswordAuthenticationToken(request.email(), request.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            httpRequest.getSession(true)
                    .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                            SecurityContextHolder.getContext());

            Client client = (Client) authentication.getPrincipal();
            return ResponseEntity.ok(new ClientLoginResponse(
                    client.getId(),
                    client.getName(),
                    client.isActive(),
                    client.getCreatedAt()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestBody VerifyEmailRequest request) {
        try {
            verifyEmailUseCase.verify(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
