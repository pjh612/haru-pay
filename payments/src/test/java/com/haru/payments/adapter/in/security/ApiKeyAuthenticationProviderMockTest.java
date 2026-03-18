package com.haru.payments.adapter.in.security;

import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationProviderMockTest {
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void authenticate_ShouldGrantAllAuthorities_WhenApiKeyMatches() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId, "test@test.com", "store", "encoded-key", "password", true, true, Instant.now());
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("valid-key", "encoded-key")).thenReturn(true);

        ApiKeyAuthenticationProvider provider = new ApiKeyAuthenticationProvider(clientRepository, passwordEncoder);
        Authentication authentication = provider.authenticate(new ApiKeyAuthenticationToken("valid-key", clientId.toString(), null, null));

        Set<String> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());

        assertThat(authorities).contains("ROLE_PAYMENT_CLIENT", "PAYMENT_PREPARE", "PAYMENT_CONFIRM");
    }

    @Test
    void authenticate_ShouldThrowBadCredentials_WhenApiKeyDoesNotMatch() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId, "test@test.com", "store", "encoded-key", "password", true, true, Instant.now());
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("invalid-key", "encoded-key")).thenReturn(false);

        ApiKeyAuthenticationProvider provider = new ApiKeyAuthenticationProvider(clientRepository, passwordEncoder);

        assertThatThrownBy(() -> provider.authenticate(new ApiKeyAuthenticationToken("invalid-key", clientId.toString(), null, null)))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticate_ShouldThrowBadCredentials_WhenApiKeyIsNull() {
        ApiKeyAuthenticationProvider provider = new ApiKeyAuthenticationProvider(clientRepository, passwordEncoder);

        assertThatThrownBy(() -> provider.authenticate(new ApiKeyAuthenticationToken(null, UUID.randomUUID().toString(), null, null)))
                .isInstanceOf(BadCredentialsException.class);
    }
}
