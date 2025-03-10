package com.haru.common;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RequiresNewExecutor {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
