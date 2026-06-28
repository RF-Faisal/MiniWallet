package com.miniwallet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_wallet_id")
    private Long fromWalletId;

    @Column(name = "to_wallet_id")
    private Long toWalletId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public Transaction() {}

    public Transaction(Long id, Long fromWalletId, Long toWalletId, BigDecimal amount, TransactionType type, TransactionStatus status, OffsetDateTime createdAt) {
        this.id = id; this.fromWalletId = fromWalletId; this.toWalletId = toWalletId;
        this.amount = amount; this.type = type; this.status = status; this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (status == null) status = TransactionStatus.SUCCESS;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFromWalletId() { return fromWalletId; }
    public void setFromWalletId(Long fromWalletId) { this.fromWalletId = fromWalletId; }
    public Long getToWalletId() { return toWalletId; }
    public void setToWalletId(Long toWalletId) { this.toWalletId = toWalletId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}