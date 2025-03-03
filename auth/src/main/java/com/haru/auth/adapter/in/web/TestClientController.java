package com.haru.auth.adapter.in.web;

import com.haru.auth.application.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestClientController {
    private final ClientService clientService;

    @GetMapping
    public void test() {
        clientService.save(RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("payments")
                .clientSecret("{noop}secret")
                .clientName("payments")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantTypes(it -> it.addAll(Set.of(AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.REFRESH_TOKEN)))
                .redirectUris(it -> it.addAll(Set.of("http://payments:8071/login/oauth2/code/payments-oidc", "http://payments:8071/authorized")))
                .scopes(it -> it.addAll(Set.of("openid", "members.read")))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build());
    }
}
