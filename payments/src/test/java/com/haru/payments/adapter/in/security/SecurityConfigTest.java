package com.haru.payments.adapter.in.security;

import com.haru.payments.support.ContainerizedIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityConfigTest extends ContainerizedIntegrationTest {

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Test
    void payerSecurityFilterChain_ShouldContainSingleOauth2RedirectFilter() {
        SecurityFilterChain payerChain = springSecurityFilterChain.getFilterChains().stream()
                .filter(chain -> chain.getFilters().stream().anyMatch(OAuth2AuthorizationRequestRedirectFilter.class::isInstance))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Payer security filter chain not found"));

        long redirectFilterCount = payerChain.getFilters().stream()
                .filter(OAuth2AuthorizationRequestRedirectFilter.class::isInstance)
                .count();

        long loginFilterCount = payerChain.getFilters().stream()
                .filter(OAuth2LoginAuthenticationFilter.class::isInstance)
                .count();

        assertThat(redirectFilterCount).isEqualTo(1);
        assertThat(loginFilterCount).isEqualTo(1);
    }
}
