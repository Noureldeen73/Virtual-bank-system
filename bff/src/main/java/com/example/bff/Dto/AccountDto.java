package com.example.bff.Dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountDto {
    private UUID userId;
    private AccountType accountType;
    private BigDecimal balance;
    private String accountNumber;
    private BigDecimal InitialBalance;
    private AccountStatus accountStatus;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public BigDecimal getInitialBalance() {
        return InitialBalance;
    }
    public void setInitialBalance(BigDecimal initialBalance) {
        InitialBalance = initialBalance;
    }
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
