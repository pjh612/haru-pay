package com.haru.money.application;

import com.fasterxml.uuid.Generators;
import com.haru.money.domain.model.Money;
import com.haru.money.domain.repository.MoneyRepository;
import com.haru.money.support.ContainerizedIntegrationTest;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
public class RedisLockTest extends ContainerizedIntegrationTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private MoneyRepository moneyRepository;


    @Test
    public void spinLock() throws InterruptedException {
        //given
        UUID memberId = Generators.timeBasedEpochGenerator().generate();
        Money money = Money.createNew(memberId);
        money.load(BigDecimal.valueOf(5000));
        moneyRepository.save(money);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        //when
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    funcWithSpinLock(memberId);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Money foundMoney = moneyRepository.findByMemberId(memberId).get();
        Assertions.assertThat(foundMoney.getBalance().compareTo(BigDecimal.valueOf(4000))).isEqualTo(0);
    }

    @Transactional
    public void funcWithSpinLock(UUID memberId) {
        String key = "key";
        boolean locked = false;
        while (!locked) {
            locked = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(key, "lock", Duration.ofMillis(3000))
            );
            if (!locked) {
                Thread.yield();
            }
        }

        try {
            Money money = moneyRepository.findByMemberId(memberId)
                    .orElseThrow(RuntimeException::new);
            money.decrease(BigDecimal.valueOf(10));

            moneyRepository.save(money);
        } finally {
            redisTemplate.delete(key);
        }
    }

    @Test
    public void pubsubLock() throws InterruptedException {
        //given
        UUID memberId = Generators.timeBasedEpochGenerator().generate();
        Money money = Money.createNew(memberId);
        money.load(BigDecimal.valueOf(5000));
        moneyRepository.save(money);
        CountDownLatch countDownLatch = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        //when
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    func(memberId);

                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        Money foundMoney = moneyRepository.findByMemberId(memberId).get();
        Assertions.assertThat(foundMoney.getBalance().compareTo(BigDecimal.valueOf(4000))).isEqualTo(0);
    }

    @Transactional
    public void func(UUID memberId) {
        lock("key");
        Money money = moneyRepository.findByMemberId(memberId)
                .orElseThrow(RuntimeException::new);
        money.decrease(BigDecimal.valueOf(10));

        moneyRepository.save(money);
        unlock("key");
    }


    public void lock(String key) {
        RLock lock = redissonClient.getLock(key);
        try {
            lock.tryLock(10, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.unlock();
    }


    public class RedisSpinLock {
        private final RedisTemplate<String, String> redisTemplate;
        private final String lockKey;
        private final long expireTime;

        public RedisSpinLock(RedisTemplate<String, String> redisTemplate, String lockKey, long expireTime) {
            this.redisTemplate = redisTemplate;
            this.lockKey = lockKey;
            this.expireTime = expireTime;
        }

        public boolean tryLock() {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCK", Duration.ofMillis(expireTime));
            return Boolean.TRUE.equals(success);
        }

        public void unlock() {
            redisTemplate.delete(lockKey);
        }
    }
}
