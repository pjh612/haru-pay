package com.haru.banking.adapter.out.persistence.jpa.entity;

import com.haru.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "registered_bank_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisteredAccountJpaEntity {
    @Id
    @UuidV7Generator
    private UUID id;
    private UUID memberId;
    private String bankName;
    private String accountNumber;
    private boolean isValid;
}
