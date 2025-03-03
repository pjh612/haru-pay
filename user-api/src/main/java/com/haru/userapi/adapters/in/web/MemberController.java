package com.haru.userapi.adapters.in.web;

import com.haru.userapi.application.MemberSignupUseCase;
import com.haru.userapi.application.QueryMemberUseCase;
import com.haru.userapi.application.dto.MemberSignupRequest;
import com.haru.userapi.application.dto.MemberSignupResponse;
import com.haru.userapi.application.dto.QueryMemberResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberSignupUseCase memberSignupUsecase;
    private final QueryMemberUseCase queryMemberUsecase;

    public MemberController(MemberSignupUseCase memberSignupUsecase, QueryMemberUseCase queryMemberUsecase) {
        this.memberSignupUsecase = memberSignupUsecase;
        this.queryMemberUsecase = queryMemberUsecase;
    }

    @PostMapping
    public MemberSignupResponse signup(@RequestBody MemberSignupRequest request) {
        return memberSignupUsecase.signup(request);
    }

    @GetMapping
    public QueryMemberResponse findByUsername(@RequestParam String username) {
        return queryMemberUsecase.queryByUsername(username);
    }

    @GetMapping("/{id}")
    public QueryMemberResponse findByMemberId(@PathVariable String id) {
        return queryMemberUsecase.queryById(UUID.fromString(id));
    }

}
