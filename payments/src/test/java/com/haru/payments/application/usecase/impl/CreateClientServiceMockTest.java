package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.cache.VerificationTokenRepository;
import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.CreateClientRequest;
import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

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
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Test
    void create_ShouldReturnClientResponse_WhenRequestIsValid() {
        String email = "test@test.com";
        String name = "TestClient";
        String password = "password123";
        CreateClientRequest request = new CreateClientRequest(email, name, password);

        when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encoded");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponse response = createClientService.create(request);

        assertThat(response.name()).isEqualTo(name);
        assertThat(response.isActive()).isTrue();
        assertThat(response.apiKey()).isNotNull();

        verify(passwordEncoder, times(2)).encode(any(CharSequence.class));
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(verificationTokenRepository, times(1)).save(anyString(), any(UUID.class), eq(24L));
    }
}
