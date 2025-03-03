package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.CreateClientRequest;
import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Test
    void create_ShouldReturnClientResponse_WhenRequestIsValid() {
        // Arrange
        String name = "TestClient";
        CreateClientRequest request = new CreateClientRequest(name);
        UUID id = UUID.randomUUID();
        UUID key = UUID.randomUUID();
        String encodedKey = "encoded-key";

        Client client = Client.create(id, name, encodedKey);

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn(encodedKey);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Act
        ClientResponse response = createClientService.create(request);

        // Assert
        assertThat(response.id()).isEqualTo(client.getId());
        assertThat(response.name()).isEqualTo(client.getName());
        assertThat(response.isActive()).isEqualTo(client.isActive());
        assertThat(response.createdAt()).isEqualTo(client.getCreatedAt());

        verify(passwordEncoder, times(1)).encode(any(CharSequence.class));
        verify(clientRepository, times(1)).save(any(Client.class));
    }

}