package com.haru.payments.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String PROBLEM_BASE_URI = "https://haru-pay.dev/problems/payments/";

    @ExceptionHandler(NoResourceFoundException.class)
    ProblemDetail handleNoResourceFound(NoResourceFoundException e, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                "resource-not-found",
                "Resource Not Found",
                "요청한 리소스를 찾을 수 없습니다.",
                request
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ProblemDetail handleEntityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                "entity-not-found",
                "Entity Not Found",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        if (detail.isBlank()) {
            detail = "요청 본문 검증에 실패했습니다.";
        }

        return problem(
                HttpStatus.BAD_REQUEST,
                "invalid-request-body",
                "Invalid Request Body",
                detail,
                request
        );
    }

    @ExceptionHandler(BindException.class)
    ProblemDetail handleBindException(BindException e, HttpServletRequest request) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        if (detail.isBlank()) {
            detail = "요청 파라미터 검증에 실패했습니다.";
        }

        return problem(
                HttpStatus.BAD_REQUEST,
                "invalid-request-parameter",
                "Invalid Request Parameter",
                detail,
                request
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String detail = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        if (detail.isBlank()) {
            detail = "요청 값 검증에 실패했습니다.";
        }

        return problem(
                HttpStatus.BAD_REQUEST,
                "constraint-violation",
                "Constraint Violation",
                detail,
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String detail = "요청 파라미터 타입이 올바르지 않습니다: " + e.getName();

        return problem(
                HttpStatus.BAD_REQUEST,
                "invalid-parameter-type",
                "Invalid Parameter Type",
                detail,
                request
        );
    }

    @ExceptionHandler(MissingRequestValueException.class)
    ProblemDetail handleMissingRequestValue(MissingRequestValueException e, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "missing-required-value",
                "Missing Required Value",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "malformed-json",
                "Malformed JSON",
                "요청 본문을 해석할 수 없습니다.",
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "invalid-argument",
                "Invalid Argument",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail handleIllegalState(IllegalStateException e, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                "invalid-state",
                "Invalid State",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail handleAuthentication(AuthenticationException e, HttpServletRequest request) {
        return problem(
                HttpStatus.UNAUTHORIZED,
                "unauthorized",
                "Unauthorized",
                "인증 정보가 유효하지 않습니다.",
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return problem(
                HttpStatus.FORBIDDEN,
                "forbidden",
                "Forbidden",
                "요청한 작업에 대한 권한이 없습니다.",
                request
        );
    }

    @ExceptionHandler(RuntimeException.class)
    ProblemDetail handleRuntime(RuntimeException e, HttpServletRequest request) {
        log.error("Unhandled runtime exception", e);
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "internal-error",
                "Internal Server Error",
                "서버 내부 오류가 발생했습니다.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception e, HttpServletRequest request) {
        log.error("Unexpected error", e);
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "unexpected-error",
                "Unexpected Server Error",
                "예기치 않은 서버 오류가 발생했습니다.",
                request
        );
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
}
