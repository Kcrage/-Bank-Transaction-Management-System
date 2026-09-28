package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int transactionId;
    private int accountId;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;

    public Transaction(int transactionId, int accountId, String transactionType,
                       BigDecimal amount, String description, LocalDateTime transactionDate) {
        this.transactionId   = transactionId;
        this.accountId       = accountId;
        this.transactionType = transactionType;
        this.amount          = amount;
        this.description     = description;
        this.transactionDate = transactionDate;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    @Override
    public String toString() {
        return String.format(
            "  [%d] %-14s ₹%-10.2f  %-35s  %s",
            transactionId,
            transactionType,
            amount,
            (description != null ? description : ""),
            transactionDate
        );
    }
}
