package com.haru.money;

import com.haru.money.support.ContainerizedIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
class MoneyApplicationTests extends ContainerizedIntegrationTest {

    @Test
    void contextLoads() {
    }

}
