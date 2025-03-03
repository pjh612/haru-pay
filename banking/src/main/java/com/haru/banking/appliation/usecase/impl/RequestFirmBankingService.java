package com.haru.banking.appliation.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.banking.appliation.client.FirmBankingClient;
import com.haru.banking.appliation.client.dto.ExternalFirmBankingRequest;
import com.haru.banking.appliation.client.dto.ExternalFirmBankingResponse;
import com.haru.banking.appliation.dto.RequestFirmBankingRequest;
import com.haru.banking.appliation.dto.RequestFirmBankingResponse;
import com.haru.banking.appliation.usecase.RequestFirmBankingUseCase;
import com.haru.banking.domain.model.FirmBankingRequest;
import com.haru.banking.domain.repository.FirmBankingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestFirmBankingService implements RequestFirmBankingUseCase {
    private final FirmBankingClient firmBankingClient;
    private final FirmBankingRequestRepository firmBankingRequestRepository;

    @Override
    @Transactional
    public RequestFirmBankingResponse request(RequestFirmBankingRequest request) {
        FirmBankingRequest firmBankingRequest = firmBankingRequestRepository.save(FirmBankingRequest.createNew(
                Generators.timeBasedEpochGenerator().generate(),
                request.fromBankName(),
                request.fromBankAccountNumber(),
                request.toBankName(),
                request.toBankAccountNumber(),
                request.amount()
        ));
        ExternalFirmBankingRequest externalFirmbankingRequest = new ExternalFirmBankingRequest(
                request.fromBankName(),
                request.fromBankAccountNumber(),
                request.toBankName(),
                request.toBankAccountNumber(),
                request.amount()
        );
        ExternalFirmBankingResponse externalFirmBankingResponse = firmBankingClient.requestFirmBanking(externalFirmbankingRequest);
        if (externalFirmBankingResponse.resultCode() == 1) {
            firmBankingRequest.success();
        } else {
            firmBankingRequest.fail();
        }
        firmBankingRequestRepository.save(firmBankingRequest);

        return new RequestFirmBankingResponse(firmBankingRequest.getId(), firmBankingRequest.getStatus().name());
    }
}
