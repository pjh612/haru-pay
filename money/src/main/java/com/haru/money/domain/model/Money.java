package com.haru.money.domain.model;


import com.haru.money.domain.WalletBalanceException;

import java.math.BigDecimal;
import java.util.UUID;

public class Money {
    private UUID id;
    private UUID memberId;
    private BigDecimal balance;

    public static Money createNew(UUID memberId) {
        return new Money(null, memberId, BigDecimal.ZERO);
    }

    public BigDecimal load(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    public BigDecimal decrease(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new WalletBalanceException("잔액이 부족합니다.");
        }
        this.balance = this.balance.subtract(amount);

        return this.balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public Money(UUID id, UUID memberId, BigDecimal balance) {
        this.id = id;
        this.memberId = memberId;
        this.balance = balance;
    }
}
