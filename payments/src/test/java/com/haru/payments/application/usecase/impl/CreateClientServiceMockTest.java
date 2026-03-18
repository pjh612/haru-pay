package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.CreateClientRequest;
import com.haru.payments.application.port.out.cache.EmailVerificationTokenRepository;
import com.haru.payments.application.port.out.event.ClientEventPort;
import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateClientServiceMockTest {
    @InjectMocks
    private CreateClientService createClientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClientEventPort clientEventPort;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Test
    void create_ShouldReturnClientResponse_WhenRequestIsValid() {
        String email = "test@test.com";
        String name = "TestClient";
        String password = "password123";
        CreateClientRequest request = new CreateClientRequest(email, name, password);

        when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encoded");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailVerificationTokenRepository).save(any(), any());
        doNothing().when(clientEventPort).sendEmailVerificationRequested(any(), any(), any(), any());

        ClientResponse response = createClientService.create(request);

        assertThat(response.name()).isEqualTo(name);
        assertThat(response.isActive()).isTrue();
        assertThat(response.apiKey()).isNotNull();

        verify(passwordEncoder, times(2)).encode(any(CharSequence.class));
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(emailVerificationTokenRepository, times(1)).save(any(), any());
        verify(clientEventPort, times(1)).sendEmailVerificationRequested(any(), any(), any(), any());
    }
}
