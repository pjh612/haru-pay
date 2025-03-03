package com.haru.userapi.domain.repository;


import com.haru.userapi.domain.models.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findById(UUID id);

    Optional<Member> findByUsername(String username);
}
