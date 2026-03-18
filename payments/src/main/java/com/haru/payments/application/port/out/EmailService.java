package com.haru.payments.application.port.out;

/**
 * 이메일 발송 서비스 인터페이스
 */
public interface EmailService {
    /**
     * 이메일 인증 메일을 발송합니다.
     *
     * @param to               수신자 이메일
     * @param clientName       클라이언트 이름
     * @param verificationToken 인증 토큰
     * @param clientId         클라이언트 ID
     */
    void sendVerificationEmail(String to, String clientName, String verificationToken, String clientId);
}
