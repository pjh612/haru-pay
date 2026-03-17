package com.haru.payments.adapter.in.security;

import com.haru.payments.application.usecase.QueryClientUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final QueryClientUseCase queryClientUseCase;
    private final PasswordEncoder passwordEncoder;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter(authenticationConfiguration.getAuthenticationManager());
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/clients").permitAll()
                                .requestMatchers("/error").permitAll()
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/payment/prepare").hasAuthority("PAYMENT_PREPARE")
                                .requestMatchers(HttpMethod.POST, "/api/payment/confirm").hasAuthority("PAYMENT_CONFIRM")
                                .requestMatchers("/api/payment-result/subscribe").hasAuthority("PAYMENT_CONFIRM")
                                .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login.loginPage("/oauth2/authorization/payments-oidc"))
                .oauth2Client(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        return new ApiKeyAuthenticationProvider(queryClientUseCase, passwordEncoder);
    }
}
