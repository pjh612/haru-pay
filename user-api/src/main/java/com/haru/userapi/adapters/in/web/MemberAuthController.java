package com.haru.userapi.adapters.in.web;

import com.haru.userapi.application.MemberAuthUseCase;
import com.haru.userapi.application.dto.MemberAuthRequest;
import com.haru.userapi.application.dto.MemberAuthResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member/authentication")
public class MemberAuthController {
    private final MemberAuthUseCase memberAuthUsecase;

    public MemberAuthController(MemberAuthUseCase memberAuthUsecase) {
        this.memberAuthUsecase = memberAuthUsecase;
    }

    @PostMapping
    public MemberAuthResponse authenticate(@RequestBody MemberAuthRequest request) {
        return memberAuthUsecase.auth(request.username(), request.password());
    }
}
