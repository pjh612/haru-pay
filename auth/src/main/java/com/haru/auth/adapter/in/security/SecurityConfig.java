package com.haru.auth.adapter.in.security;


import com.haru.auth.application.service.MemberService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.public-key}")
    private RSAPublicKey publicKey;

    @Value("${jwt.private-key}")
    private RSAPrivateKey privateKey;


    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .oauth2AuthorizationServer((authorizationServer) -> {
                    http.securityMatcher(authorizationServer.getEndpointsMatcher());
                    authorizationServer
                            .oidc(Customizer.withDefaults())
                            .authorizationEndpoint(endpoint -> endpoint
                                    .errorResponseHandler((request, response, exception) ->
                                            response.sendRedirect("/error?message=invalid_client")
                                    )
                            );
                })
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/member/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                ));

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationProvider memberAuthenticationProvider) {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/member/login", "/error", "/.well-known/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(it -> it
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler())
                )
                .authenticationProvider(memberAuthenticationProvider);
        return http.build();
    }

    private boolean isAjax(jakarta.servlet.http.HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private AuthenticationSuccessHandler successHandler() {
        var delegate = new SavedRequestAwareAuthenticationSuccessHandler();
        return (request, response, authentication) -> {
            if (isAjax(request)) {
                var wrapper = new HttpServletResponseWrapper(response) {
                    String redirectUrl;

                    @Override
                    public void sendRedirect(String location) {
                        this.redirectUrl = location;
                    }
                };
                delegate.onAuthenticationSuccess(request, wrapper, authentication);
                String redirectUrl = wrapper.redirectUrl != null ? wrapper.redirectUrl : "/";

                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"redirectUrl\":\"" + redirectUrl + "\"}");
            } else {
                delegate.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }

    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            String errorType = exception instanceof AuthenticationServiceException ? "service" : "credentials";
            if (isAjax(request)) {
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"" + errorType + "\"}");
            } else {
                response.sendRedirect("/member/login?error=" + errorType);
            }
        };
    }

    @Bean
    AuthenticationProvider memberAuthenticationProvider(MemberService memberService) {
        return new MemberAuthenticationProvider(memberService);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return (context) -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Authentication authenticationToken = context.getPrincipal();

                Object principal = authenticationToken.getPrincipal();
                if (principal instanceof MemberPrincipal) {
                    context.getClaims().claim("id", ((MemberPrincipal) principal).getId());
                }
            } else if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
                Authentication authenticationToken = context.getPrincipal();

                Object principal = authenticationToken.getPrincipal();
                if (principal instanceof MemberPrincipal) {
                    context.getClaims().claim("id", ((MemberPrincipal) principal).getId());
                }

            }
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("haru-auth-key")
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources()
                        .atCommonLocations()
                );
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
