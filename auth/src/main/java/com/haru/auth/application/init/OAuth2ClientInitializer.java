package com.haru.auth.application.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2ClientInitializer implements ApplicationRunner {
    private final RegisteredClientRepository clientRepository;

    @Override
    public void run(ApplicationArguments args) {
        registerIfAbsent("payments", "{noop}secret", Set.of(
                "http://payments:8071/login/oauth2/code/payments-oidc",
                "http://localhost:8071/login/oauth2/code/payments-oidc"
        ), Set.of("openid", "members.read"));
    }

    private void registerIfAbsent(String clientId, String clientSecret, Set<String> redirectUris, Set<String> scopes) {
        if (clientRepository.findByClientId(clientId) != null) {
            log.info("OAuth2 client '{}' already registered, skipping.", clientId);
            return;
        }

        clientRepository.save(RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientName(clientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantTypes(types -> types.addAll(Set.of(
                        AuthorizationGrantType.AUTHORIZATION_CODE,
                        AuthorizationGrantType.REFRESH_TOKEN
                )))
                .redirectUris(uris -> uris.addAll(redirectUris))
                .scopes(s -> s.addAll(scopes))
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build())
                .build());

        log.info("OAuth2 client '{}' registered successfully.", clientId);
    }
}
