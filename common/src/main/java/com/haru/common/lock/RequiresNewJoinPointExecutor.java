package com.haru.common.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RequiresNewJoinPointExecutor {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
