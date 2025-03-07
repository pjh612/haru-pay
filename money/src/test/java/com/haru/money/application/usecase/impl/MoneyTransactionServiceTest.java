package com.haru.money.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.money.domain.model.Money;
import com.haru.money.domain.repository.MoneyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ActiveProfiles("test")
@SpringBootTest
class MoneyTransactionServiceTest {
    @Autowired
    private MoneyTransactionEdaService moneyTransactionService;
    @Autowired
    private MoneyRepository moneyRepository;

    @Test
    void decreaseMoneyMultiThreadTest() throws InterruptedException {
        //given
        UUID memberId = Generators.timeBasedEpochGenerator().generate();
        Money money = Money.createNew(memberId);
        money.load(BigDecimal.valueOf(5000));
        moneyRepository.save(money);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        //when
        for (int i = 0; i < 100; i++) {
            UUID requestId = Generators.timeBasedEpochGenerator().generate();
            executorService.submit(() -> {
                try {
                    moneyTransactionService.decrease(requestId, memberId, BigDecimal.valueOf(10));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        Money foundMoney = moneyRepository.findByMemberId(memberId).get();
        Assertions.assertThat(foundMoney.getBalance().compareTo(BigDecimal.valueOf(4000))).isEqualTo(0);

    }

    @Test
    void loadMoneyMultiThreadTest() throws InterruptedException {
        //given
        UUID memberId = Generators.timeBasedEpochGenerator().generate();
        Money money = Money.createNew(memberId);
        moneyRepository.save(money);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        //when
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    moneyTransactionService.increaseMoney(memberId, BigDecimal.valueOf(10));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        //then
        Money foundMoney = moneyRepository.findByMemberId(memberId).get();
        Assertions.assertThat(foundMoney.getBalance().compareTo(BigDecimal.valueOf(1000))).isEqualTo(0);
    }
}