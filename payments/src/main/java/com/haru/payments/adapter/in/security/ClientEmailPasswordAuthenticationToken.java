package com.haru.payments.adapter.in.security;

import com.haru.payments.domain.model.Client;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class ClientEmailPasswordAuthenticationToken extends AbstractAuthenticationToken {
    private final String email;
    private final String password;
    private final Client client;

    ClientEmailPasswordAuthenticationToken(
            String email,
            Client client,
            Collection<? extends GrantedAuthority> authorities,
            boolean authenticated) {
        super(authorities);
        this.email = email;
        this.password = null;
        this.client = client;
        super.setAuthenticated(authenticated);
    }

    public ClientEmailPasswordAuthenticationToken(String email, String password) {
        super(Collections.emptyList());
        this.email = email;
        this.password = password;
        this.client = null;
        setAuthenticated(false);
    }

    public ClientEmailPasswordAuthenticationToken(Client client, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.email = client.getEmail();
        this.password = null;
        this.client = client;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return client != null ? client : email;
    }

    @Override
    public String getName() {
        return email;
    }
}
