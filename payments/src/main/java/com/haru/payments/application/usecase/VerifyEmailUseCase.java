package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.VerifyEmailRequest;

public interface VerifyEmailUseCase {
    void verify(VerifyEmailRequest request);
}
