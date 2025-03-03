package com.haru.userapi.adapters.out.persistence.jpa.converter;

import com.haru.userapi.adapters.out.persistence.jpa.entity.entity.MemberEntity;
import com.haru.userapi.domain.models.Gender;
import com.haru.userapi.domain.models.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MemberEntityConverter {

    public MemberEntity toEntity(Member member) {
        return new MemberEntity(member.getId(),
                member.getUsername(),
                member.getPassword(),
                member.getName(),
                member.getGender().name()
        );
    }

    public Member toDomain(MemberEntity memberEntity) {
        return new Member(memberEntity.getId(),
                memberEntity.getUsername(),
                memberEntity.getPassword(),
                memberEntity.getName(),
                Gender.valueOf(memberEntity.getGender())
        );
    }
}
