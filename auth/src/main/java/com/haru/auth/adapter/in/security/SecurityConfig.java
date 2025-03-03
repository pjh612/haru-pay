package com.haru.auth.adapter.in.security;


import com.haru.auth.application.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, AuthenticationProvider memberAuthenticationProvider) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer().oidc(Customizer.withDefaults());
        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer.oidc(Customizer.withDefaults())    // Enable OpenID Connect 1.0
                )
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .anyRequest().authenticated()
                ).authenticationProvider(memberAuthenticationProvider);

        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoint = new LinkedHashMap<>();
        entryPoint.put(new ParameterRequestMatcher("client_id", "payments"), new RedirectLoginUrlAuthenticationEntryPoint("/member/login"));


        return http
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new DelegatingAuthenticationEntryPoint(entryPoint),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)

                ))
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationProvider memberAuthenticationProvider) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/member/login","/test").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(it -> it
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                )
                .authenticationProvider(memberAuthenticationProvider);
        return http.build();
    }

    private SavedRequestAwareAuthenticationSuccessHandler successHandler() {
        SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        savedRequestAwareAuthenticationSuccessHandler.setTargetUrlParameter("redirect_uri");

        return savedRequestAwareAuthenticationSuccessHandler;
    }

    @Bean("memberAuthenticationProvider")
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
