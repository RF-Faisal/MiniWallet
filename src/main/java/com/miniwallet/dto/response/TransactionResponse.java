package com.miniwallet.dto.response;

import com.miniwallet.entity.TransactionStatus;
import com.miniwallet.entity.TransactionType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionResponse {
    private Long id;
    private Long fromWalletId;
    private Long toWalletId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private OffsetDateTime createdAt;

    public TransactionResponse() {}

    public TransactionResponse(Long id, Long fromWalletId, Long toWalletId, BigDecimal amount,
                                TransactionType type, TransactionStatus status, OffsetDateTime createdAt) {
        this.id = id; this.fromWalletId = fromWalletId; this.toWalletId = toWalletId;
        this.amount = amount; this.type = type; this.status = status; this.createdAt = createdAt;
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