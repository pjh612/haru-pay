package com.haru.banking.domain.repository;

import com.haru.banking.domain.model.FirmBankingRequest;

public interface FirmBankingRequestRepository {
    FirmBankingRequest save(FirmBankingRequest firmBankingRequest);
}
