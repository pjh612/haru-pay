package com.haru.auth.adapter.in.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;


public class ParameterRequestMatcher implements RequestMatcher {
    private final String parameterName;
    private final String parameterValue;

    public ParameterRequestMatcher(String parameterName, String parameterValue) {
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String value = request.getParameter(parameterName);
        return parameterValue.equals(value);
    }
}

