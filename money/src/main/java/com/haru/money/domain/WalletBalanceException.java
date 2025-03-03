package com.haru.money.domain;

public class WalletBalanceException extends RuntimeException {
    public WalletBalanceException(String message) {
        super(message);
    }
}
