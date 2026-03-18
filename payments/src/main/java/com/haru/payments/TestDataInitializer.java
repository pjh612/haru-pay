package com.haru.payments;

import com.fasterxml.uuid.Generators;
import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInitializer {

    private static final String TEST_EMAIL = "test@harupay.com";
    private static final String TEST_PASSWORD = "test1234";
    private static final String TEST_NAME = "테스트 클라이언트";

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (clientRepository.findByEmail(TEST_EMAIL).isPresent()) {
            return;
        }

        UUID id = Generators.timeBasedEpochGenerator().generate();
        UUID rawApiKey = Generators.timeBasedEpochGenerator().generate();

        Client client = new Client(
                id,
                TEST_EMAIL,
                TEST_NAME,
                passwordEncoder.encode(rawApiKey.toString()),
                passwordEncoder.encode(TEST_PASSWORD),
                true,
                true,
                java.time.Instant.now()
        );

        clientRepository.save(client);

        log.info("""

                ========================================
                 테스트 계정이 생성되었습니다
                ----------------------------------------
                 이메일    : {}
                 비밀번호  : {}
                 클라이언트 ID : {}
                 API Key   : {}
                ========================================
                """, TEST_EMAIL, TEST_PASSWORD, id, rawApiKey);
    }
}
