package com.haru.payments.adapter.in.security;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.usecase.QueryClientUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationProviderMockTest {
    @Mock
    private QueryClientUseCase queryClientUseCase;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void authenticate_ShouldGrantPrepareAndConfirmAuthorities_WhenApiKeyMatches() {
        UUID clientId = UUID.randomUUID();
        when(queryClientUseCase.queryById(clientId)).thenReturn(new ClientResponse(clientId, "store", "encoded-key", true, Instant.now()));
        when(passwordEncoder.matches("valid-key", "encoded-key")).thenReturn(true);

        ApiKeyAuthenticationProvider provider = new ApiKeyAuthenticationProvider(queryClientUseCase, passwordEncoder);
        Authentication authentication = provider.authenticate(new ApiKeyAuthenticationToken("valid-key", clientId.toString(), null, null));

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());

        assertThat(authorities).contains("ROLE_PAYMENT_CLIENT", "PAYMENT_PREPARE", "PAYMENT_CONFIRM");
    }

    @Test
    void authenticate_ShouldNotGrantPrepareOrConfirmAuthorities_WhenApiKeyDoesNotMatch() {
        UUID clientId = UUID.randomUUID();
        when(queryClientUseCase.queryById(clientId)).thenReturn(new ClientResponse(clientId, "store", "encoded-key", true, Instant.now()));
        when(passwordEncoder.matches("invalid-key", "encoded-key")).thenReturn(false);

        ApiKeyAuthenticationProvider provider = new ApiKeyAuthenticationProvider(queryClientUseCase, passwordEncoder);
        Authentication authentication = provider.authenticate(new ApiKeyAuthenticationToken("invalid-key", clientId.toString(), null, null));

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());

        assertThat(authorities).contains("ROLE_PAYMENT_CLIENT");
        assertThat(authorities).doesNotContain("PAYMENT_PREPARE", "PAYMENT_CONFIRM");
    }
}
