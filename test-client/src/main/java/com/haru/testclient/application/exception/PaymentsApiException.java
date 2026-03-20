package com.haru.testclient.application.exception;

public class PaymentsApiException extends RuntimeException {
    private final int status;
    private final String errorType;
    private final String typeUri;
    private final String title;
    private final String detail;

    public PaymentsApiException(int status,
                                String errorType,
                                String typeUri,
                                String title,
                                String detail) {
        super(detail);
        this.status = status;
        this.errorType = errorType;
        this.typeUri = typeUri;
        this.title = title;
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getTypeUri() {
        return typeUri;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }
}
