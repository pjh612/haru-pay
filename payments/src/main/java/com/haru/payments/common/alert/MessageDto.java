package com.haru.payments.common.alert;

public record MessageDto(String targetId, String type, Object body) {
    private static final String DEFAULT_MESSAGE_TYPE = "message";

    public static MessageDto of(String targetId, Object message) {
        return new MessageDto(targetId, DEFAULT_MESSAGE_TYPE, message);
    }

    public static MessageDto of(String targetId, String type, Object message) {
        return new MessageDto(targetId, type, message);
    }
}
