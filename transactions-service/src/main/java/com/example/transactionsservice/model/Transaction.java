package com.example.transactionsservice.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Transaction ID cannot be blank")
    @Column(nullable = false)
    private UUID from_account_id;

    @NotBlank(message = "Transaction ID cannot be blank")
    @Column(nullable = false)
    private UUID to_account_id;

    @NotBlank(message = "Amount cannot be blank")
    @Column(nullable = false, scale = 15, precision = 2)
    private BigDecimal amount;


    @Column(nullable = true, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private TransactionStatus status = TransactionStatus.INITIATED;

    @Column
    private LocalDateTime created_at = LocalDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFrom_account_id() {
        return from_account_id;
    }

    public void setFrom_account_id(UUID from_account_id) {
        this.from_account_id = from_account_id;
    }

    public UUID getTo_account_id() {
        return to_account_id;
    }

    public void setTo_account_id(UUID to_account_id) {
        this.to_account_id = to_account_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
