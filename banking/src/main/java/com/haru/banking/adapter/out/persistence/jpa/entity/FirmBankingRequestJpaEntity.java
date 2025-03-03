package com.haru.banking.adapter.out.persistence.jpa.entity;

import com.haru.banking.domain.model.FirmBankingStatus;
import com.haru.common.hibernate.annotations.UuidV7Generator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@Table(name = "firm_banking_request")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FirmBankingRequestJpaEntity {
    @Id
    @UuidV7Generator
    private UUID id;
    private String fromBankName;
    private String fromBankAccountNumber;
    private String toBankName;
    private String toBankAccountNumber;
    private BigDecimal amount;
    private FirmBankingStatus status;
}
