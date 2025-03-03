package com.haru.banking.appliation.usecase;

import com.haru.banking.appliation.dto.RequestFirmBankingRequest;
import com.haru.banking.appliation.dto.RequestFirmBankingResponse;

public interface RequestFirmBankingUseCase {
    RequestFirmBankingResponse request(RequestFirmBankingRequest request);
}
