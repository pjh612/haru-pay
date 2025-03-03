package com.haru.banking.appliation.client;

import com.haru.banking.appliation.client.dto.ExternalFirmBankingRequest;
import com.haru.banking.appliation.client.dto.ExternalFirmBankingResponse;

public interface FirmBankingClient {
    ExternalFirmBankingResponse requestFirmBanking(ExternalFirmBankingRequest request);
}
