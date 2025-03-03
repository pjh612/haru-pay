package com.haru.auth.adapter.in.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class RedirectLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    public RedirectLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String redirectUri = request.getParameter("redirect_uri");

        return super.determineUrlToUseForThisRequest(request, response, exception) + "?redirect_uri=" + redirectUri;
    }
}
