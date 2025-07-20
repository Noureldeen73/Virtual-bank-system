package com.example.transactionsservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountDto {
    private UUID accountId;
    private BigDecimal balance;

    public AccountDto() {
    }

    public AccountDto(UUID accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
