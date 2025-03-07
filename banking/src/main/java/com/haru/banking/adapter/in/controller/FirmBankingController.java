package com.haru.banking.adapter.in.controller;

import com.haru.banking.appliation.dto.RequestFirmBankingRequest;
import com.haru.banking.appliation.dto.RequestFirmBankingResponse;
import com.haru.banking.appliation.usecase.RequestFirmBankingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/firm-banking")
public class FirmBankingController {
    private final RequestFirmBankingUseCase requestFirmBankingUseCase;

    @PostMapping
    public RequestFirmBankingResponse requestFirmBanking(@RequestBody RequestFirmBankingRequest request) {
        return requestFirmBankingUseCase.request(request);
    }
}
