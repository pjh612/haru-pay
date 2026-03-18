package com.haru.payments.adapter.out.mail;

import com.haru.payments.application.port.out.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:noreply@harupay.com}")
    private String fromEmail;

    @Value("${developer-portal.url:http://localhost:3001}")
    private String developerPortalUrl;

    @Override
    public void sendVerificationEmail(String to, String clientName, String verificationToken, String clientId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("clientName", clientName);
            context.setVariable("verificationLink", buildVerificationLink(verificationToken));
            context.setVariable("developerPortalUrl", developerPortalUrl);

            String htmlContent = templateEngine.process("email/verification", context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[HaruPay] 이메일 인증을 완료해주세요");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    private String buildVerificationLink(String verificationToken) {
        return developerPortalUrl + "/verify-email?token=" + verificationToken;
    }
}
