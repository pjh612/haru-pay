package com.haru.payments.adapter.in.security;

import com.haru.payments.domain.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionConfigTest {

    @Test
    void springSessionDefaultRedisSerializer_ShouldRoundTripSecurityContextWithoutJacksonAnnotations() {
        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setBeanClassLoader(getClass().getClassLoader());

        RedisSerializer<Object> serializer = sessionConfig.springSessionDefaultRedisSerializer();
        Client client = new Client(
                UUID.randomUUID(),
                "client@test.com",
                "client-name",
                "api-key",
                "encoded-password",
                true,
                true,
                Instant.now()
        );
        ClientEmailPasswordAuthenticationToken authentication = new ClientEmailPasswordAuthenticationToken(
                client,
                List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
        SecurityContextImpl context = new SecurityContextImpl(authentication);

        byte[] serialized = serializer.serialize(context);
        Object restored = serializer.deserialize(serialized);

        assertThat(restored).isInstanceOf(SecurityContextImpl.class);

        Authentication restoredAuthentication = ((SecurityContextImpl) restored).getAuthentication();
        assertThat(restoredAuthentication).isInstanceOf(ClientEmailPasswordAuthenticationToken.class);
        assertThat(restoredAuthentication.isAuthenticated()).isTrue();
        assertThat(restoredAuthentication.getAuthorities())
                .extracting(authority -> authority.getAuthority())
                .containsExactly("ROLE_CLIENT");

        Object principal = restoredAuthentication.getPrincipal();
        assertThat(principal).isInstanceOf(Client.class);

        Client restoredClient = (Client) principal;
        assertThat(restoredClient.getId()).isEqualTo(client.getId());
        assertThat(restoredClient.getEmail()).isEqualTo(client.getEmail());
        assertThat(restoredClient.getName()).isEqualTo(client.getName());
        assertThat(restoredClient.getApiKey()).isNull();
        assertThat(restoredClient.getPassword()).isNull();
        assertThat(restoredClient.isEmailVerified()).isEqualTo(client.isEmailVerified());
        assertThat(restoredClient.isActive()).isEqualTo(client.isActive());
    }

}
