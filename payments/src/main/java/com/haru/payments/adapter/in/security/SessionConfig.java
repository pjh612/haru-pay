package com.haru.payments.adapter.in.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.payments.domain.model.Client;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableRedisHttpSession(redisNamespace = SessionConfig.SESSION_NAMESPACE)
public class SessionConfig implements BeanClassLoaderAware {

    public static final String SESSION_NAMESPACE = "payments:";
    public static final String SESSION_COOKIE_NAME = "PAYMENTS_SESSION";

    private ClassLoader loader;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(SESSION_COOKIE_NAME);
        return serializer;
    }

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> invalidatedSessionSafeFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                try {
                    filterChain.doFilter(request, response);
                } catch (IllegalStateException exception) {
                    if (!"Session was invalidated".equals(exception.getMessage())) {
                        throw exception;
                    }
                }
            }
        });
        registration.setOrder(SessionRepositoryFilter.DEFAULT_ORDER - 1);
        return registration;
    }

    /**
     * Note that the bean name for this bean is intentionally
     * {@code springSessionDefaultRedisSerializer}. It must be named this way to override
     * the default {@link RedisSerializer} used by Spring Session.
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        BasicPolymorphicTypeValidator.Builder validatorBuilder = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.haru.payments")
                .allowIfSubType("org.springframework.security")
                .allowIfSubType("java.util")
                .allowIfSubType("java.math")
                .allowIfSubType("java.net.URL")
                .allowIfSubType("java.time");

        List<JacksonModule> modules = SecurityJacksonModules.getModules(this.loader, validatorBuilder);
        BasicPolymorphicTypeValidator typeValidator = validatorBuilder.build();

        return GenericJacksonJsonRedisSerializer.create(builder ->
                builder.enableDefaultTyping(typeValidator)
                        .customize(mb -> mb.addModules(modules)
                                .addMixIn(Client.class, ClientMixin.class)
                                .addMixIn(ClientEmailPasswordAuthenticationToken.class,
                                        ClientEmailPasswordAuthenticationTokenMixin.class))
        );
    }

    /*
     * @see
     * org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang
     * .ClassLoader)
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }

    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.NONE,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    private abstract static class ClientMixin {
        @JsonCreator
        ClientMixin(
                @JsonProperty("id") java.util.UUID id,
                @JsonProperty("email") String email,
                @JsonProperty("name") String name,
                @JsonProperty("emailVerified") boolean emailVerified,
                @JsonProperty("active") boolean active,
                @JsonProperty("createdAt") java.time.Instant createdAt) {
        }

        @JsonProperty("id")
        abstract java.util.UUID getId();

        @JsonProperty("email")
        abstract String getEmail();

        @JsonProperty("name")
        abstract String getName();

        @JsonProperty("emailVerified")
        abstract boolean isEmailVerified();

        @JsonProperty("active")
        abstract boolean isActive();

        @JsonProperty("createdAt")
        abstract java.time.Instant getCreatedAt();
    }

    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private abstract static class ClientEmailPasswordAuthenticationTokenMixin {
        @JsonCreator
        ClientEmailPasswordAuthenticationTokenMixin(
                @JsonProperty("email") String email,
                @JsonProperty("client") Client client,
                @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
                @JsonProperty("authenticated") boolean authenticated) {
        }
    }

}
