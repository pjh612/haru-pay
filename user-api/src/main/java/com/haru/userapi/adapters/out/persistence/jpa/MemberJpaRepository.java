package com.haru.userapi.adapters.out.persistence.jpa;

import com.haru.userapi.adapters.out.persistence.jpa.entity.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, UUID> {
    Optional<MemberEntity> findByUsername(String username);
}
