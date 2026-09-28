package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {

    private int accountId;
    private String accountName;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public Account(int accountId, String accountName, BigDecimal balance, LocalDateTime createdAt) {
        this.accountId   = accountId;
        this.accountName = accountName;
        this.balance     = balance;
        this.createdAt   = createdAt;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format(
            "Account[id=%d, name='%s', balance=₹%.2f, created=%s]",
            accountId, accountName, balance, createdAt
        );
    }
}
