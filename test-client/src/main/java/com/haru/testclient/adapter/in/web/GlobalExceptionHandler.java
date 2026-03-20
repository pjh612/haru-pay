package com.haru.testclient.adapter.in.web;

import com.haru.testclient.application.exception.PaymentsApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String PROBLEM_BASE_URI = "https://haru-pay.dev/problems/test-client/";

    @ExceptionHandler(PaymentsApiException.class)
    ProblemDetail handlePaymentsApiException(PaymentsApiException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(e.getStatus());
        if (status == null) {
            status = HttpStatus.BAD_GATEWAY;
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, e.getDetail());
        problem.setType(resolveTypeUri(e));
        problem.setTitle(e.getTitle());
        problem.setProperty("errorType", e.getErrorType());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        if (detail.isBlank()) {
            detail = "요청 본문 검증에 실패했습니다.";
        }

        return problem(HttpStatus.BAD_REQUEST, "invalid-request-body", "Invalid Request Body", detail, request);
    }

    @ExceptionHandler(BindException.class)
    ProblemDetail handleBindException(BindException e, HttpServletRequest request) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        if (detail.isBlank()) {
            detail = "요청 파라미터 검증에 실패했습니다.";
        }

        return problem(HttpStatus.BAD_REQUEST, "invalid-request-parameter", "Invalid Request Parameter", detail, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String detail = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        if (detail.isBlank()) {
            detail = "요청 값 검증에 실패했습니다.";
        }

        return problem(HttpStatus.BAD_REQUEST, "constraint-violation", "Constraint Violation", detail, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "invalid-parameter-type",
                "Invalid Parameter Type",
                "요청 파라미터 타입이 올바르지 않습니다: " + e.getName(),
                request
        );
    }

    @ExceptionHandler(MissingRequestValueException.class)
    ProblemDetail handleMissingRequestValue(MissingRequestValueException e, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "missing-required-value", "Missing Required Value", e.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "invalid-argument", "Invalid Argument", e.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail handleIllegalState(IllegalStateException e, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, "invalid-state", "Invalid State", e.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception e, HttpServletRequest request) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "Internal Server Error", "서버 내부 오류가 발생했습니다.", request);
    }

    private ProblemDetail problem(HttpStatus status,
                                  String type,
                                  String title,
                                  String detail,
                                  HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(PROBLEM_BASE_URI + type));
        problem.setTitle(title);
        problem.setProperty("errorType", type);
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    private URI resolveTypeUri(PaymentsApiException e) {
        String typeUri = e.getTypeUri();
        if (typeUri != null && !typeUri.isBlank()) {
            return URI.create(typeUri);
        }
        return URI.create(PROBLEM_BASE_URI + e.getErrorType());
    }
}
