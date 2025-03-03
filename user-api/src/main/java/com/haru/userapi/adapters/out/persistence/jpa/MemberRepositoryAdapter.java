package com.haru.userapi.adapters.out.persistence.jpa;

import com.haru.userapi.adapters.out.persistence.jpa.converter.MemberEntityConverter;
import com.haru.userapi.adapters.out.persistence.jpa.entity.entity.MemberEntity;
import com.haru.userapi.domain.models.Member;
import com.haru.userapi.domain.repository.MemberRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class MemberRepositoryAdapter implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberEntityConverter memberEntityConverter;

    public MemberRepositoryAdapter(MemberJpaRepository memberJpaRepository, MemberEntityConverter memberEntityConverter) {
        this.memberJpaRepository = memberJpaRepository;
        this.memberEntityConverter = memberEntityConverter;
    }

    @Override
    public Member save(Member member) {
        MemberEntity entity = memberJpaRepository.save(memberEntityConverter.toEntity(member));

        return memberEntityConverter.toDomain(entity);
    }

    public Optional<Member> findById(UUID id) {
        return memberJpaRepository.findById(id)
                .map(memberEntityConverter::toDomain)
                .or(Optional::empty);
    }

    public Optional<Member> findByUsername(String username) {
        return memberJpaRepository.findByUsername(username)
                .map(memberEntityConverter::toDomain)
                .or(Optional::empty);
    }
}
