package com.haru.payments.adapter.in.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

public final class ApiProblemResponseWriter {
    private static final String PROBLEM_BASE_URI = "https://haru-pay.dev/problems/payments/";

    private ApiProblemResponseWriter() {
    }

    public static void write(HttpServletRequest request,
                             HttpServletResponse response,
                             HttpStatus status,
                             String type,
                             String title,
                             String detail) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        String payload = "{"
                + "\"type\":\"" + escape(PROBLEM_BASE_URI + type) + "\"," 
                + "\"title\":\"" + escape(title) + "\"," 
                + "\"status\":" + status.value() + ","
                + "\"detail\":\"" + escape(detail) + "\"," 
                + "\"instance\":\"" + escape(request.getRequestURI()) + "\"," 
                + "\"errorType\":\"" + escape(type) + "\"," 
                + "\"path\":\"" + escape(request.getRequestURI()) + "\""
                + "}";

        response.getWriter().write(payload);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
