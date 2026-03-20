package com.haru.payments.adapter.in.security;

import com.haru.payments.domain.repository.ClientRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsUtils;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final RequestMatcher API_KEY_REQUESTS = request -> {
        String method = request.getMethod();
        String path = request.getServletPath();

        if (HttpMethod.POST.matches(method)) {
            return path.equals("/api/payment/prepare") || path.equals("/api/payment/confirm");
        }

        if (HttpMethod.GET.matches(method)) {
            return path.equals("/api/payment-result/subscribe") || path.matches("/api/payment/[^/]+$");
        }

        return false;
    };

    private static final RequestMatcher PAYER_REQUESTS = request -> {
        String path = request.getServletPath();
        return path.startsWith("/pay/")
                || path.equals("/api/payment/request")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/");
    };

    private static final RequestMatcher DEVELOPER_CENTER_REQUESTS = request -> {
        String path = request.getServletPath();
        return path.startsWith("/api/clients")
                || path.equals("/error")
                || path.equals("/error-page")
                || path.startsWith("/js/")
                || path.equals("/favicon.ico");
    };

    private final ClientRepository clientRepository;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private AuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return (request, response, authException) -> ApiProblemResponseWriter.write(
                request,
                response,
                HttpStatus.UNAUTHORIZED,
                "unauthorized",
                "Unauthorized",
                "인증 정보가 유효하지 않습니다."
        );
    }

    private AccessDeniedHandler apiAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> ApiProblemResponseWriter.write(
                request,
                response,
                HttpStatus.FORBIDDEN,
                "forbidden",
                "Forbidden",
                "요청한 작업에 대한 권한이 없습니다."
        );
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(
                new ApiKeyAuthenticationProvider(clientRepository, passwordEncoder()),
                new ClientEmailPasswordAuthenticationProvider(clientRepository, passwordEncoder())
        ));
    }

    @Bean
    @Order(1)
    SecurityFilterChain paymentApiSecurityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        AuthenticationEntryPoint apiAuthenticationEntryPoint = apiAuthenticationEntryPoint();
        AccessDeniedHandler apiAccessDeniedHandler = apiAccessDeniedHandler();
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter(authenticationManager, apiAuthenticationEntryPoint);
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(API_KEY_REQUESTS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext(securityContext -> securityContext.securityContextRepository(new NullSecurityContextRepository()))
                .requestCache(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(apiAuthenticationEntryPoint)
                        .accessDeniedHandler(apiAccessDeniedHandler)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/payment/prepare").hasAuthority("PAYMENT_PREPARE")
                                .requestMatchers(HttpMethod.POST, "/api/payment/confirm").hasAuthority("PAYMENT_CONFIRM")
                                .requestMatchers(HttpMethod.GET, "/api/payment/*").hasAuthority("PAYMENT_CONFIRM")
                                .requestMatchers("/api/payment-result/subscribe").hasAuthority("PAYMENT_CONFIRM")
                                .anyRequest().denyAll())
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain payerSecurityFilterChain(HttpSecurity http) {
        AuthenticationEntryPoint apiAuthenticationEntryPoint = apiAuthenticationEntryPoint();
        AccessDeniedHandler apiAccessDeniedHandler = apiAccessDeniedHandler();
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(PAYER_REQUESTS)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                                .requestMatchers("/api/payment/request", "/pay/**").access((authentication, context) -> {
                                    var auth = authentication.get();
                                    return new AuthorizationDecision(
                                            auth != null
                                                    && auth.isAuthenticated()
                                                    && auth.getPrincipal() instanceof OidcUser
                                    );
                                })
                                .anyRequest().denyAll())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                apiAuthenticationEntryPoint,
                                request -> request.getServletPath().startsWith("/api/")
                        )
                        .defaultAccessDeniedHandlerFor(
                                (request, response, accessDeniedException) -> response.sendRedirect("/oauth2/authorization/payments-oidc"),
                                request -> request.getServletPath().startsWith("/pay/")
                        )
                        .defaultAccessDeniedHandlerFor(
                                apiAccessDeniedHandler,
                                request -> request.getServletPath().startsWith("/api/")
                        )
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/oauth2/authorization/payments-oidc")
                        .failureHandler((request, response, exception) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage())));

        return http.build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain developerCenterSecurityFilterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(DEVELOPER_CENTER_REQUESTS)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .requestMatchers("/api/clients/me", "/api/clients/me/regenerate-api-key").hasRole("CLIENT")
                                .requestMatchers("/api/clients", "/api/clients/login", "/api/clients/logout", "/api/clients/verify-email").permitAll()
                                .requestMatchers("/error", "/error-page").permitAll()
                                .requestMatchers("/js/**").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    @Order(4)
    SecurityFilterChain fallbackSecurityFilterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().denyAll());

        return http.build();
    }
}
