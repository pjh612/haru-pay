package com.haru.userapi.application.impl;

import com.haru.userapi.application.MemberAuthUseCase;
import com.haru.userapi.application.dto.MemberAuthResponse;
import com.haru.userapi.domain.models.Member;
import com.haru.userapi.domain.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberAuthService implements MemberAuthUseCase {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberAuthResponse auth(String id, String password) {
        Member member = memberRepository.findByUsername(id)
                .orElseThrow(()->new EntityNotFoundException("아이디/비밀번호를 확인해주세요."));

        if(!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("아이디/비밀번호를 확인해주세요.");
        }

        return new MemberAuthResponse(member.getId(), member.getName());
    }
}
