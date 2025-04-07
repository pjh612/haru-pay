package com.haru.money.common.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class RedisLockAspect {
    private final RedissonClient redissonClient;
    private final ExpressionParser parser;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RedisLockAspect(RedissonClient redissonClient, ApplicationEventPublisher applicationEventPublisher) {
        this.redissonClient = redissonClient;
        this.applicationEventPublisher = applicationEventPublisher;
        this.parser = new SpelExpressionParser();
    }

    @Around("@annotation(com.haru.money.common.lock.RedisLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String lockKey = getKey(joinPoint.getArgs(), signature.getParameterNames(), redisLock.key());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(redisLock.waitTime(), redisLock.leaseTime(), redisLock.unit())) {
                return joinPoint.proceed();
            } else {
                throw new RuntimeException("already locked");
            }
        } finally {
            applicationEventPublisher.publishEvent(new UnLockEvent(lock));
        }
    }

    private String getKey(Object[] args, String[] parameterNames, String keyExpression) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void unlock(UnLockEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("Releasing lock for key: {}", event.lock.getName());
        }
        event.lock.unlock();
    }

    public record UnLockEvent(RLock lock) {
    }
}
