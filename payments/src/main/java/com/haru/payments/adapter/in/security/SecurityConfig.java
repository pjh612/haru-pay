package com.haru.payments.adapter.in.security;

import com.haru.payments.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ClientRepository clientRepository;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(
                new ApiKeyAuthenticationProvider(clientRepository, passwordEncoder()),
                new ClientEmailPasswordAuthenticationProvider(clientRepository, passwordEncoder())
        ));
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter(authenticationManager);
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/clients/me", "/api/clients/me/regenerate-api-key").hasRole("CLIENT")
                                .requestMatchers("/api/clients", "/api/clients/login", "/api/clients/logout", "/api/clients/verify-email").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/js/harupay.js").permitAll()
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers("/developer", "/developer/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/payment/prepare").hasAuthority("PAYMENT_PREPARE")
                                .requestMatchers(HttpMethod.POST, "/api/payment/confirm").hasAuthority("PAYMENT_CONFIRM")
                                .requestMatchers(HttpMethod.GET, "/api/payment/*").hasAuthority("PAYMENT_CONFIRM")
                                .requestMatchers("/api/payment-result/subscribe").hasAuthority("PAYMENT_CONFIRM")
                                .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/payments-oidc"))
                .oauth2Client(Customizer.withDefaults())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
