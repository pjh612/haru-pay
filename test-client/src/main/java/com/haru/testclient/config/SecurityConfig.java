package com.haru.testclient.config;

import com.haru.testclient.adapter.in.security.JsonLoginAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) {
        var securityContextRepository = new HttpSessionSecurityContextRepository();

        JsonLoginAuthenticationFilter jsonLoginFilter = new JsonLoginAuthenticationFilter(authenticationManager);
        jsonLoginFilter.setSecurityContextRepository(securityContextRepository);
        jsonLoginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            writeJsonResponse(response, HttpServletResponse.SC_OK,
                    "{\"username\":\"" + escapeJson(authentication.getName()) + "\"}");
        });
        jsonLoginFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            if (exception instanceof AuthenticationServiceException) {
                writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "{\"error\":\"잘못된 로그인 요청입니다.\"}");
                return;
            }

            writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "{\"error\":\"아이디 또는 비밀번호가 올바르지 않습니다.\"}");
        });

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/assets/**").permitAll()
                        .requestMatchers("/demo/api/login", "/demo/api/csrf", "/demo/api/config").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/demo/api/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) ->
                                writeJsonResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
                                        "{\"error\":\"인증이 필요합니다.\"}"))
                        .accessDeniedHandler((req, res, accessDeniedException) ->
                                writeJsonResponse(res, HttpServletResponse.SC_FORBIDDEN,
                                        "{\"error\":\"요청이 거부되었습니다.\"}"))
                )
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .addFilterAt(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                );

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    UserDetailsService userDetailsService() {
        var buyer = User.withUsername("buyer")
                .password("{noop}1234")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(buyer);
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void writeJsonResponse(HttpServletResponse response, int status, String payload) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(payload);
    }
}
